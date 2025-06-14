package com.graduationproject.graduationproject.controller;

import com.graduationproject.graduationproject.entity.*;
import com.graduationproject.graduationproject.entity.body.PageBody1;
import com.graduationproject.graduationproject.entity.body.UserBodyChangePassword;
import com.graduationproject.graduationproject.repository.FertilizerRepository;
import com.graduationproject.graduationproject.repository.ProductRepository;
import com.graduationproject.graduationproject.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/farmStaff")
// 农场员工功能
public class FarmStaffController {

    @Autowired
    private ProductService productService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ConsumptionService consumptionService;

    @Autowired
    private ProduceService produceService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private FertilizerService fertilizerService;

    @Autowired
    private RestaurantMaterialService restaurantMaterialService;

    @Autowired
    private SaleNoService saleNoService;

    @Autowired
    private SaleService saleService;

    @PostMapping("/initProduct")
    private Map initProduct(@RequestBody String productType) {
        //System.out.println("post success!" + productType);

        int page = 1;
        int pages = 0;
        List<Integer> pageList = new ArrayList<Integer>();

        List<Product> productList = new ArrayList<Product>();
        productList = productService.findByProductType(productType);

        if (productList.size() / 5 >= 5) {
            for (int i = 0; i < 5; i++) {
                pageList.add(i + 1);
            }

            if (productList.size() % 5 != 0) {
                pages = (productList.size() + 5) / 5;
            } else {
                pages = productList.size() / 5;
            }
            productList = productList.subList(0, 5);
        } else {
            if (!productList.isEmpty()) {
                if (productList.size() % 5 != 0) {
                    pages = (productList.size() + 5) / 5;
                } else {
                    pages = productList.size() / 5;
                }

                for (int i = 0; i < pages; i++) {
                    pageList.add(i + 1);
                }

                if (productList.size() < 5) {
                    productList = productList.subList(0, productList.size());
                } else {
                    productList = productList.subList(0, 5);
                }
            }
        }

        PageBody1 pageBody1 = new PageBody1();
        pageBody1.setPage(page);
        pageBody1.setPages(pages);
        pageBody1.setPageList(pageList);

        return Map.of("productList", productList, "pageBody1", pageBody1);
    }

    @PostMapping("/doPage/{productType}")
    // 农产品分页
    public Map doPage(@RequestBody PageBody1 pageBody1,
                      @PathVariable String productType) {
        //System.out.println("post success!" + pageBody1.getPage() + productType);

        List<Integer> pageList = new ArrayList<Integer>();
        List<Product> productList = new ArrayList<Product>();

        productList = productService.findByProductType(productType);
        if (productList.isEmpty()) {
            pageBody1.setPage(0);
            pageBody1.setPages(0);
            pageBody1.setPageList(pageList);
        } else {
            if ((double) productList.size() / 5 > 5.0) {
                if (productList.size() % 5 != 0) {
                    pageBody1.setPages((productList.size() + 5) / 5);
                } else {
                    pageBody1.setPages(productList.size() / 5);
                }

                if (pageBody1.getPage() <= pageBody1.getPages()) {
                    if (pageBody1.getPage() + 2 > pageBody1.getPages()) {
                        for (int i = pageBody1.getPages() - 5; i < pageBody1.getPages(); i++) {
                            pageList.add(i + 1);
                        }
                    } else {
                        pageList.add(pageBody1.getPage() - 2);
                        pageList.add(pageBody1.getPage() - 1);
                        pageList.add(pageBody1.getPage());
                        pageList.add(pageBody1.getPage() + 1);
                        pageList.add(pageBody1.getPage() + 2);
                    }
                } else {
                    pageBody1.setPage(pageBody1.getPages());
                    for (int i = pageBody1.getPages() - 5; i < pageBody1.getPages(); i++) {
                        pageList.add(i + 1);
                    }
                }
            } else {
                if (productList.size() % 5 != 0) {
                    pageBody1.setPages((productList.size() + 5) / 5);
                } else {
                    pageBody1.setPages(productList.size() / 5);
                }

                if (pageBody1.getPage() <= pageBody1.getPages()) {
                    for (int i = 0; i < pageBody1.getPages(); i++) {
                        pageList.add(i + 1);
                    }
                } else {
                    pageBody1.setPage(pageBody1.getPages());
                    for (int i = 0; i < pageBody1.getPages(); i++) {
                        pageList.add(i + 1);
                    }
                }
            }

            productList = productList.subList(pageBody1.getPage() * 5 - 5, pageBody1.getPage() * 5 > productList.size() ? productList.size() : pageBody1.getPage() * 5);
            pageBody1.setPageList(pageList);
        }

        return Map.of("productList", productList, "pageBody1", pageBody1);
    }

    @PostMapping("changePassword")
    // 修改密码
    public Map changePassword(@RequestBody UserBodyChangePassword userBodyChangePassword) {
        //System.out.println("post success!" + userBody1.getUsername());

        String message = "";
        Staff staff = new Staff();

        staff = staffService.findByUsername(userBodyChangePassword.getUsername());
        if (!passwordEncoder.matches(userBodyChangePassword.getPassword(), staff.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "旧密码错误！");
        } else {
            staff.setPassword(passwordEncoder.encode(userBodyChangePassword.getNewPassword()));
            staffService.updateStaff(staff);
            message = "修改成功！";
        }

        return Map.of("message", message);
    }

    @PostMapping("addProduct")
    // 添加农场品
    public Map addProduct(@RequestBody Product product) {
        //System.out.println("post success!" + product.getProductType());

        String message = "";

        if (productService.findByName(product.getName()) != null) {
            message = "该农产品已存在！";
        } else {
            product.setBaseAmount(0);
            productService.save(product);
            message = "添加成功！";
        }

        return Map.of("message", message);
    }

    @PostMapping("deleteProduct")
    // 删除农产品
    public Map deleteProduct(@RequestBody Product product) {
        //System.out.println("post success!" + product.getProductType() + product.getId());

        Product product1 = productService.findByName(product.getName());
        productService.deleteProduct(product1);

        return Map.of("message", "删除成功！");
    }

    @PostMapping("modifyProduct")
    // 修改农产品
    public Map modifyProduct(@RequestBody Product product) {
        //System.out.println("post success!" + product.getName());

        productService.save(product);

        return Map.of("message", "修改成功！");
    }

    @PostMapping("abnormalConsumption")
    // 异常消耗
    public Map abnormalConsumption(@RequestBody Product product) {
        //System.out.println("post success!"+product.getAmount());

        String message = "提交成功！";
        Product product1 = productService.findByName(product.getName());

        BigDecimal bigDecimal = new BigDecimal(Double.toString(product.getAmount()));
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(product1.getAmount()));
        BigDecimal bigDecimal2 = new BigDecimal(Double.toString(product1.getBaseAmount()));
        if (product.getProductType().equals(Product.productType1)) {
            product1.setAmount(bigDecimal1.subtract(bigDecimal).doubleValue());
        } else if (product.getProductType().equals(Product.productType2)) {
            product1.setAmount(bigDecimal1.subtract(bigDecimal).doubleValue());
        } else if (product.getProductType().equals(Product.productType3)) {
            product1.setAmount(bigDecimal1.subtract(bigDecimal).doubleValue());
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "产品类型错误！");
        }
        productService.save(product1);

        return Map.of("message", message);
    }

    @PostMapping("produce")
    // 生产农产品
    public Map produce(@RequestBody Product product) {
        //System.out.println("post success!" + product.getAmount());

        String message = "提交成功！";
        Product product1 = productService.findByName(product.getName());

        BigDecimal bigDecimal = new BigDecimal(Double.toString(product.getAmount()));
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(product1.getAmount()));
        BigDecimal bigDecimal2 = new BigDecimal(Double.toString(product1.getBaseAmount()));
        if (product.getProductType().equals(Product.productType1)) {
            product1.setBaseAmount(bigDecimal2.add(bigDecimal).doubleValue());
        } else if (product.getProductType().equals(Product.productType2)) {
            product1.setAmount(bigDecimal1.add(bigDecimal).doubleValue());
        } else if (product.getProductType().equals(Product.productType3)) {
            product1.setAmount(bigDecimal1.add(bigDecimal).doubleValue());
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "产品类型错误！");
        }
        productService.save(product1);

        return Map.of("message", message);
    }

    @PostMapping("abnormalConsumption1")
    // 异常消耗（原料）
    public Map abnormalConsumption1(@RequestBody Product product) {
        //System.out.println("post success!"+product.getAmount());

        String message = "提交成功！";
        Product product1 = productService.findByName(product.getName());

        BigDecimal bigDecimal = new BigDecimal(Double.toString(product.getAmount()));
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(product1.getAmount()));
        BigDecimal bigDecimal2 = new BigDecimal(Double.toString(product1.getBaseAmount()));
        if (product.getProductType().equals(Product.productType1)) {
            product1.setBaseAmount(bigDecimal2.subtract(bigDecimal).doubleValue());
        } else if (product.getProductType().equals(Product.productType2)) {
            product1.setAmount(bigDecimal1.subtract(bigDecimal).doubleValue());
        } else if (product.getProductType().equals(Product.productType3)) {
            product1.setBaseAmount(bigDecimal2.subtract(bigDecimal).doubleValue());
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "产品类型错误！");
        }
        productService.save(product1);

        return Map.of("message", message);
    }

    @PostMapping("addPurchase")
    // 采购原料
    public Map addPurchase(@RequestBody Purchase purchase) {
        //System.out.println("post success!" + purchase.getAmount() + purchase.getPrice() + purchase.getProduct().getName());

        Purchase purchase1 = new Purchase();
        Product product = productService.findByName(purchase.getProduct().getName());

        BigDecimal bigDecimal = new BigDecimal(Double.toString(purchase.getAmount()));
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(product.getAmount()));
        BigDecimal bigDecimal2 = new BigDecimal(Double.toString(product.getBaseAmount()));
        if (product.getProductType().equals(Product.productType1)) {
            product.setBaseAmount(bigDecimal2.add(bigDecimal).doubleValue());
        } else if (product.getProductType().equals(Product.productType2)) {
            product.setAmount(bigDecimal1.add(bigDecimal).doubleValue());
        } else if (product.getProductType().equals(Product.productType3)) {
            product.setBaseAmount(bigDecimal2.add(bigDecimal).doubleValue());
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "产品类型错误！");
        }
        productService.save(product);

        purchase1.setAmount(purchase.getAmount());
        purchase1.setPrice(purchase.getPrice());
        purchase1.setProduct(product);
        purchaseService.save(purchase1);

        return Map.of("message", "提交成功！");
    }

    @PostMapping("produce1")
    // 生产农产品
    public Map produce1(@RequestBody Product product) {
        //System.out.println("post success!" + product.getAmount());

        String message = "提交成功！";
        Product product1 = productService.findByName(product.getName());
        BigDecimal bigDecimal = new BigDecimal(product.getAmount());
        BigDecimal bigDecimal1 = new BigDecimal(product1.getAmount());
        BigDecimal bigDecimal2 = new BigDecimal(product1.getBaseAmount());
        if (product.getProductType().equals(Product.productType1)) {
            product1.setAmount(bigDecimal1.add(bigDecimal).doubleValue());
            product1.setBaseAmount(bigDecimal2.subtract(bigDecimal).doubleValue());
        } else if (product.getProductType().equals(Product.productType2)) {
            product1.setAmount(bigDecimal1.add(bigDecimal).doubleValue());
        } else if (product.getProductType().equals(Product.productType3)) {
            product1.setAmount(bigDecimal1.add(bigDecimal).doubleValue());
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "产品类型错误！");
        }
        productService.save(product1);

        return Map.of("message", message);
    }

    @PostMapping("initFertilizer")
    // 初始化饲料肥料
    public Map initFertilizer(@RequestBody String fertilizerType) {
        //System.out.println("post success!" + fertilizerType);

        int page = 1;
        int pages = 0;
        List<Integer> pageList = new ArrayList<Integer>();
        List<Fertilizer> fertilizerList = new ArrayList<Fertilizer>();

        fertilizerList = fertilizerService.findByFertilizerType(fertilizerType);
        if (fertilizerList.size() / 5 >= 5) {
            for (int i = 0; i < 5; i++) {
                pageList.add(i + 1);
            }

            if (fertilizerList.size() % 5 != 0) {
                pages = (fertilizerList.size() + 5) / 5;
            } else {
                pages = fertilizerList.size() / 5;
            }
            fertilizerList = fertilizerList.subList(0, 5);
        } else {
            if (!fertilizerList.isEmpty()) {
                if (fertilizerList.size() % 5 != 0) {
                    pages = (fertilizerList.size() + 5) / 5;
                } else {
                    pages = fertilizerList.size() / 5;
                }

                for (int i = 0; i < pages; i++) {
                    pageList.add(i + 1);
                }

                if (fertilizerList.size() < 5) {
                    fertilizerList = fertilizerList.subList(0, fertilizerList.size());
                } else {
                    fertilizerList = fertilizerList.subList(0, 5);
                }
            }
        }

        PageBody1 pageBody1 = new PageBody1();
        pageBody1.setPage(page);
        pageBody1.setPages(pages);
        pageBody1.setPageList(pageList);

        return Map.of("fertilizerList", fertilizerList, "pageBody1", pageBody1);
    }

    @PostMapping("/doPage1/{fertilizerType}")
    // 肥料饲料分页
    public Map doPage1(@RequestBody PageBody1 pageBody1,
                       @PathVariable String fertilizerType) {
        //System.out.println("post success!" + pageBody1.getPage() + productType);

        List<Integer> pageList = new ArrayList<Integer>();
        List<Fertilizer> fertilizerList = new ArrayList<Fertilizer>();

        fertilizerList = fertilizerService.findByFertilizerType(fertilizerType);
        if (fertilizerList.isEmpty()) {
            pageBody1.setPage(0);
            pageBody1.setPages(0);
            pageBody1.setPageList(pageList);
        } else {
            if ((double) fertilizerList.size() / 5 > 5.0) {
                if (fertilizerList.size() % 5 != 0) {
                    pageBody1.setPages((fertilizerList.size() + 5) / 5);
                } else {
                    pageBody1.setPages(fertilizerList.size() / 5);
                }

                if (pageBody1.getPage() <= pageBody1.getPages()) {
                    if (pageBody1.getPage() + 2 > pageBody1.getPages()) {
                        for (int i = pageBody1.getPages() - 5; i < pageBody1.getPages(); i++) {
                            pageList.add(i + 1);
                        }
                    } else {
                        pageList.add(pageBody1.getPage() - 2);
                        pageList.add(pageBody1.getPage() - 1);
                        pageList.add(pageBody1.getPage());
                        pageList.add(pageBody1.getPage() + 1);
                        pageList.add(pageBody1.getPage() + 2);
                    }
                } else {
                    pageBody1.setPage(pageBody1.getPages());
                    for (int i = pageBody1.getPages() - 5; i < pageBody1.getPages(); i++) {
                        pageList.add(i + 1);
                    }
                }
            } else {
                if (fertilizerList.size() % 5 != 0) {
                    pageBody1.setPages((fertilizerList.size() + 5) / 5);
                } else {
                    pageBody1.setPages(fertilizerList.size() / 5);
                }

                if (pageBody1.getPage() <= pageBody1.getPages()) {
                    for (int i = 0; i < pageBody1.getPages(); i++) {
                        pageList.add(i + 1);
                    }
                } else {
                    pageBody1.setPage(pageBody1.getPages());
                    for (int i = 0; i < pageBody1.getPages(); i++) {
                        pageList.add(i + 1);
                    }
                }
            }

            fertilizerList = fertilizerList.subList(pageBody1.getPage() * 5 - 5, pageBody1.getPage() * 5 > fertilizerList.size() ? fertilizerList.size() : pageBody1.getPage() * 5);
            pageBody1.setPageList(pageList);
        }

        return Map.of("fertilizerList", fertilizerList, "pageBody1", pageBody1);
    }

    @PostMapping("addPurchase1")
    // 采购饲料肥料
    public Map addPurchase1(@RequestBody Purchase purchase) {
        //System.out.println("post success!" + purchase.getAmount() + purchase.getPrice() + purchase.getProduct().getName());

        Purchase purchase1 = new Purchase();

        Fertilizer fertilizer = fertilizerService.findByName(purchase.getFertilizer().getName());
        BigDecimal bigDecimal = new BigDecimal(Double.toString(purchase.getAmount()));
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(fertilizer.getAmount()));
        fertilizer.setAmount(bigDecimal.add(bigDecimal1).doubleValue());
        fertilizerService.save(fertilizer);

        purchase1.setAmount(purchase.getAmount());
        purchase1.setPrice(purchase.getPrice());
        purchase1.setFertilizer(fertilizer);
        purchaseService.save(purchase1);

        return Map.of("message", "提交成功！");
    }

    @PostMapping("abnormalConsumptionFertilizer")
    // 消耗饲料肥料
    public Map abnormalConsumptionFertilizer(@RequestBody Fertilizer fertilizer) {
        //System.out.println("post success!" + fertilizer.getAmount());

        Fertilizer fertilizer1 = fertilizerService.findByName(fertilizer.getName());
        BigDecimal bigDecimal = new BigDecimal(Double.toString(fertilizer.getAmount()));
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(fertilizer1.getAmount()));
        fertilizer1.setAmount(bigDecimal1.subtract(bigDecimal).doubleValue());
        fertilizerService.save(fertilizer1);

        return Map.of("message", "提交成功！");
    }

    @PostMapping("farmUse")
    // 使用饲料肥料
    public Map farmUse(@RequestBody Fertilizer fertilizer) {
        //System.out.println("post success!" + fertilizer.getAmount());

        Fertilizer fertilizer1 = fertilizerService.findByName(fertilizer.getName());
        BigDecimal bigDecimal = new BigDecimal(Double.toString(fertilizer.getAmount()));
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(fertilizer1.getAmount()));
        fertilizer1.setAmount(bigDecimal1.subtract(bigDecimal).doubleValue());
        fertilizerService.save(fertilizer1);

        return Map.of("message", "提交成功！");
    }

    @PostMapping("addFertilizer")
    // 添加饲料肥料
    public Map addFertilizer(@RequestBody Fertilizer fertilizer) {
        //System.out.println("post success!" + fertilizer.getName());

        Fertilizer fertilizer1 = fertilizerService.findByName(fertilizer.getName());
        if (fertilizer1 != null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "该" + fertilizer.getFertilizerType() + "已存在！");
        }

        fertilizerService.save(fertilizer);

        return Map.of("message", "添加成功！");
    }

    @PostMapping("deleteFertilizer")
    // 删除饲料肥料
    public Map deleteFertilizer(@RequestBody Fertilizer fertilizer) {
        //System.out.println("post success!" + fertilizer.getId() + fertilizer.getName());

        Fertilizer fertilizer1 = fertilizerService.findByName(fertilizer.getName());
        fertilizerService.deleteFertilizer(fertilizer1);

        return Map.of("message", "删除成功！");
    }

    @PostMapping("addRestaurantMaterial")
    // 添加餐厅物资
    public Map addRestaurantMaterial(@RequestBody RestaurantMaterial restaurantMaterial) {
        //System.out.println("post success!"+restaurantMaterial.getName());

        if (restaurantMaterialService.findByName(restaurantMaterial.getName()) != null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "该餐厅物资已存在！");
        }
        restaurantMaterialService.save(restaurantMaterial);

        return Map.of("message", "添加成功！");
    }

    @GetMapping("initRestaurantMaterial")
    // 初始化餐厅物资
    public Map initRestaurantMaterial() {
        //System.out.println("get success!");

        int page = 1;
        int pages = 0;
        List<Integer> pageList = new ArrayList<Integer>();
        List<RestaurantMaterial> restaurantMaterialList = new ArrayList<RestaurantMaterial>();

        restaurantMaterialList = restaurantMaterialService.findAll();
        if (restaurantMaterialList.size() / 5 >= 5) {
            for (int i = 0; i < 5; i++) {
                pageList.add(i + 1);
            }

            if (restaurantMaterialList.size() % 5 != 0) {
                pages = (restaurantMaterialList.size() + 5) / 5;
            } else {
                pages = restaurantMaterialList.size() / 5;
            }
            restaurantMaterialList = restaurantMaterialList.subList(0, 5);
        } else {
            if (!restaurantMaterialList.isEmpty()) {
                if (restaurantMaterialList.size() % 5 != 0) {
                    pages = (restaurantMaterialList.size() + 5) / 5;
                } else {
                    pages = restaurantMaterialList.size() / 5;
                }

                for (int i = 0; i < pages; i++) {
                    pageList.add(i + 1);
                }

                if (restaurantMaterialList.size() < 5) {
                    restaurantMaterialList = restaurantMaterialList.subList(0, restaurantMaterialList.size());
                } else {
                    restaurantMaterialList = restaurantMaterialList.subList(0, 5);
                }
            }
        }

        PageBody1 pageBody1 = new PageBody1();
        pageBody1.setPage(page);
        pageBody1.setPages(pages);
        pageBody1.setPageList(pageList);

        return Map.of("restaurantMaterialList", restaurantMaterialList, "pageBody1", pageBody1);
    }

    @PostMapping("/doPage2")
    // 餐厅物资分页
    public Map doPage2(@RequestBody PageBody1 pageBody1) {
        //System.out.println("post success!" + pageBody1.getPage() + productType);

        List<Integer> pageList = new ArrayList<Integer>();
        List<RestaurantMaterial> restaurantMaterialList = new ArrayList<RestaurantMaterial>();

        restaurantMaterialList = restaurantMaterialService.findAll();
        if (restaurantMaterialList.isEmpty()) {
            pageBody1.setPage(0);
            pageBody1.setPages(0);
            pageBody1.setPageList(pageList);
        } else {
            if ((double) restaurantMaterialList.size() / 5 > 5.0) {
                if (restaurantMaterialList.size() % 5 != 0) {
                    pageBody1.setPages((restaurantMaterialList.size() + 5) / 5);
                } else {
                    pageBody1.setPages(restaurantMaterialList.size() / 5);
                }

                if (pageBody1.getPage() <= pageBody1.getPages()) {
                    if (pageBody1.getPage() + 2 > pageBody1.getPages()) {
                        for (int i = pageBody1.getPages() - 5; i < pageBody1.getPages(); i++) {
                            pageList.add(i + 1);
                        }
                    } else {
                        pageList.add(pageBody1.getPage() - 2);
                        pageList.add(pageBody1.getPage() - 1);
                        pageList.add(pageBody1.getPage());
                        pageList.add(pageBody1.getPage() + 1);
                        pageList.add(pageBody1.getPage() + 2);
                    }
                } else {
                    pageBody1.setPage(pageBody1.getPages());
                    for (int i = pageBody1.getPages() - 5; i < pageBody1.getPages(); i++) {
                        pageList.add(i + 1);
                    }
                }
            } else {
                if (restaurantMaterialList.size() % 5 != 0) {
                    pageBody1.setPages((restaurantMaterialList.size() + 5) / 5);
                } else {
                    pageBody1.setPages(restaurantMaterialList.size() / 5);
                }

                if (pageBody1.getPage() <= pageBody1.getPages()) {
                    for (int i = 0; i < pageBody1.getPages(); i++) {
                        pageList.add(i + 1);
                    }
                } else {
                    pageBody1.setPage(pageBody1.getPages());
                    for (int i = 0; i < pageBody1.getPages(); i++) {
                        pageList.add(i + 1);
                    }
                }
            }

            restaurantMaterialList = restaurantMaterialList.subList(pageBody1.getPage() * 5 - 5, pageBody1.getPage() * 5 > restaurantMaterialList.size() ? restaurantMaterialList.size() : pageBody1.getPage() * 5);
            pageBody1.setPageList(pageList);
        }

        return Map.of("restaurantMaterialList", restaurantMaterialList, "pageBody1", pageBody1);
    }

    @PostMapping("addRestaurantMaterialPurchase")
    // 餐厅物资采购
    public Map addRestaurantMaterialPurchase(@RequestBody Purchase purchase) {
        //System.out.println("post success!" + purchase.getRestaurantMaterial().getName());

        RestaurantMaterial restaurantMaterial = restaurantMaterialService.findByName(purchase.getRestaurantMaterial().getName());
        BigDecimal bigDecimal = new BigDecimal(Double.toString(purchase.getAmount()));
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(restaurantMaterial.getAmount()));
        restaurantMaterial.setAmount(bigDecimal1.add(bigDecimal).doubleValue());
        restaurantMaterialService.save(restaurantMaterial);

        Purchase purchase1 = new Purchase();
        purchase1.setAmount(purchase.getAmount());
        purchase1.setPrice(purchase.getPrice());
        purchase1.setRestaurantMaterial(purchase.getRestaurantMaterial());
        purchaseService.save(purchase1);

        return Map.of("message", "提交成功！");
    }

    @PostMapping("deleteRestaurantMaterial")
    // 删除餐厅物资信息
    public Map deleteRestaurantMaterial(@RequestBody RestaurantMaterial restaurantMaterial) {
        //System.out.println("post success!" + restaurantMaterial.getName());

        RestaurantMaterial restaurantMaterial1 = restaurantMaterialService.findByName(restaurantMaterial.getName());
        restaurantMaterialService.delete(restaurantMaterial1);

        return Map.of("message", "删除成功！");
    }

    @PostMapping("consumptionRestaurantMaterial")
    // 餐厅物资异常消耗
    public Map consumptionRestaurantMaterial(@RequestBody RestaurantMaterial restaurantMaterial) {
        //System.out.println("post success!" + restaurantMaterial.getAmount());

        RestaurantMaterial restaurantMaterial1 = restaurantMaterialService.findByName(restaurantMaterial.getName());
        BigDecimal bigDecimal = new BigDecimal(Double.toString(restaurantMaterial.getAmount()));
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(restaurantMaterial1.getAmount()));
        restaurantMaterial1.setAmount(bigDecimal1.subtract(bigDecimal).doubleValue());
        restaurantMaterialService.save(restaurantMaterial1);

        return Map.of("message", "已提交！");
    }

    @PostMapping("useRestaurantMaterial")
    // 餐厅物资使用
    public Map useRestaurantMaterial(@RequestBody RestaurantMaterial restaurantMaterial) {
        //System.out.println("post success!" + restaurantMaterial.getAmount());

        RestaurantMaterial restaurantMaterial1 = restaurantMaterialService.findByName(restaurantMaterial.getName());
        BigDecimal bigDecimal = new BigDecimal(Double.toString(restaurantMaterial1.getAmount()));
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(restaurantMaterial.getAmount()));
        restaurantMaterial1.setAmount(bigDecimal1.subtract(bigDecimal).doubleValue());
        restaurantMaterialService.save(restaurantMaterial1);

        return Map.of("message", "已提交！");
    }

    @PostMapping("addSaleList")
    //
    public Map addSaleList(@RequestBody List<Sale> saleList) {
        //System.out.println("post success!" + saleList.get(0).getProduct().getName());

        int count = saleNoService.getCount();
        SaleNo saleNo = new SaleNo();
        if (count == 0) {
            saleNo.setNo("00001");
        } else {
            int maxNO = saleNoService.getMaxNO();
            saleNo.setNo(String.format("%5d", maxNO).replace(" ", "0"));
            SaleNo saleNo1 = saleNoService.findByNo(saleNo.getNo());
            saleNoService.deleteSaleNo(saleNo1);
        }
        saleNoService.save(saleNo);

        SaleNo saleNo2 = new SaleNo();
        saleNo2.setNo(String.format("%5d", (Integer.valueOf(saleNo.getNo()).intValue() + 1)).replace(" ", "0"));
        saleNoService.save(saleNo2);

        SaleNo saleNo3 = saleNoService.findByNo(saleNo.getNo());

        for (Sale sale : saleList) {
            Product product = productService.findByName(sale.getProduct().getName());
            BigDecimal bigDecimal = new BigDecimal(Double.toString(product.getAmount()));
            BigDecimal bigDecimal1 = new BigDecimal(Double.toString(sale.getAmount()));
            product.setAmount(bigDecimal.subtract(bigDecimal1).doubleValue());
            productService.save(product);

            sale.setSaleNo(saleNo3);
        }

        saleService.saveAll(saleList);

        return Map.of("message", "提交成功！");
    }
}
