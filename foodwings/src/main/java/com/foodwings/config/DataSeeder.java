package com.foodwings.config;

import com.foodwings.entity.Category;
import com.foodwings.entity.Coupon;
import com.foodwings.entity.DeliveryPartner;
import com.foodwings.entity.FoodItem;
import com.foodwings.entity.Restaurant;
import com.foodwings.entity.Role;
import com.foodwings.entity.User;
import com.foodwings.enums.FoodType;
import com.foodwings.enums.RestaurantStatus;
import com.foodwings.enums.RoleName;
import com.foodwings.repository.CategoryRepository;
import com.foodwings.repository.CouponRepository;
import com.foodwings.repository.DeliveryPartnerRepository;
import com.foodwings.repository.FoodItemRepository;
import com.foodwings.repository.RestaurantRepository;
import com.foodwings.repository.RoleRepository;
import com.foodwings.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Seeds reference and demo data on startup:
 * roles, an admin account, 10 categories, 10 restaurants, 100 food items,
 * 20 customers, 10 delivery partners and 10 coupons.
 * Seeding is idempotent and guarded so it only runs on an empty database.
 */
@Component
@Order(1)
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private static final String DEFAULT_PASSWORD = "Password@123";

    private static final String[] CATEGORY_NAMES = {
            "Pizza", "Burger", "Chinese", "South Indian", "North Indian",
            "Desserts", "Drinks", "Biryani", "Rolls", "Snacks"
    };

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodItemRepository foodItemRepository;
    private final DeliveryPartnerRepository deliveryPartnerRepository;
    private final CouponRepository couponRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository,
                      UserRepository userRepository,
                      CategoryRepository categoryRepository,
                      RestaurantRepository restaurantRepository,
                      FoodItemRepository foodItemRepository,
                      DeliveryPartnerRepository deliveryPartnerRepository,
                      CouponRepository couponRepository,
                      PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.restaurantRepository = restaurantRepository;
        this.foodItemRepository = foodItemRepository;
        this.deliveryPartnerRepository = deliveryPartnerRepository;
        this.couponRepository = couponRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedRoles();
        seedAdmin();

        if (userRepository.count() > 1) {
            log.info("Demo data already present, skipping seeding.");
            return;
        }

        List<Category> categories = seedCategories();
        List<Restaurant> restaurants = seedRestaurants();
        seedFoods(restaurants, categories);
        seedCustomers();
        seedDeliveryPartners();
        seedCoupons();
        log.info("FoodWings demo data seeded successfully.");
    }

    private void seedRoles() {
        for (RoleName name : RoleName.values()) {
            if (!roleRepository.existsByName(name)) {
                roleRepository.save(new Role(name));
            }
        }
    }

    private Role role(RoleName name) {
        return roleRepository.findByName(name).orElseThrow();
    }

    private void seedAdmin() {
        if (userRepository.findByEmail("admin@foodwings.com").isEmpty()) {
            User admin = new User();
            admin.setName("FoodWings Admin");
            admin.setEmail("admin@foodwings.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setPhone("9000000000");
            admin.addRole(role(RoleName.ADMIN));
            userRepository.save(admin);
            log.info("Seeded admin account: admin@foodwings.com / Admin@123");
        }
    }

    private List<Category> seedCategories() {
        List<Category> result = new ArrayList<>();
        for (String name : CATEGORY_NAMES) {
            Category category = categoryRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> {
                        Category c = new Category();
                        c.setName(name);
                        c.setDescription(name + " dishes");
                        c.setActive(true);
                        return categoryRepository.save(c);
                    });
            result.add(category);
        }
        return result;
    }

    private List<Restaurant> seedRestaurants() {
        String[] names = {
                "Spice Villa", "Burger Barn", "Dragon Wok", "Dosa Corner", "Punjabi Tadka",
                "Sweet Tooth", "Sip & Chill", "Biryani House", "Roll Express", "Snack Shack"
        };
        String[] cities = {"Mumbai", "Delhi", "Bengaluru", "Chennai", "Pune",
                "Hyderabad", "Kolkata", "Jaipur", "Ahmedabad", "Lucknow"};
        List<Restaurant> result = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            User owner = new User();
            owner.setName(names[i] + " Owner");
            owner.setEmail("owner" + (i + 1) + "@foodwings.com");
            owner.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            owner.setPhone("90000001" + String.format("%02d", i));
            owner.addRole(role(RoleName.RESTAURANT_OWNER));
            owner = userRepository.save(owner);

            Restaurant restaurant = new Restaurant();
            restaurant.setName(names[i]);
            restaurant.setDescription("Delicious food from " + names[i]);
            restaurant.setAddress(names[i] + " Street, Block " + (i + 1));
            restaurant.setCity(cities[i]);
            restaurant.setPhone("80000001" + String.format("%02d", i));
            restaurant.setEmail("contact" + (i + 1) + "@" + names[i].toLowerCase().replace(" ", "") + ".com");
            restaurant.setOpeningTime(LocalTime.of(9, 0));
            restaurant.setClosingTime(LocalTime.of(23, 0));
            restaurant.setStatus(RestaurantStatus.APPROVED);
            restaurant.setActive(true);
            restaurant.setRating(Math.round((3.5 + ThreadLocalRandom.current().nextDouble()) * 10.0) / 10.0);
            restaurant.setOwner(owner);
            result.add(restaurantRepository.save(restaurant));
        }
        return result;
    }

    private void seedFoods(List<Restaurant> restaurants, List<Category> categories) {
        String[] dishes = {
                "Margherita", "Farmhouse", "Classic Cheeseburger", "Veg Whopper", "Hakka Noodles",
                "Chilli Paneer", "Masala Dosa", "Idli Sambar", "Butter Chicken", "Paneer Tikka",
                "Chocolate Brownie", "Gulab Jamun", "Cold Coffee", "Fresh Lime Soda", "Chicken Biryani",
                "Veg Biryani", "Egg Roll", "Paneer Roll", "French Fries", "Spring Rolls"
        };
        List<FoodItem> foods = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Restaurant restaurant = restaurants.get(i % restaurants.size());
            Category category = categories.get(i % categories.size());
            String dish = dishes[i % dishes.length];
            BigDecimal price = BigDecimal.valueOf(80 + (i % 20) * 15L);
            BigDecimal discount = BigDecimal.valueOf((i % 4) * 5L);
            FoodItem food = new FoodItem();
            food.setName(dish + " #" + (i + 1));
            food.setDescription("Tasty " + dish + " freshly prepared at " + restaurant.getName());
            food.setPrice(price);
            food.setDiscount(discount);
            food.setFoodType(i % 3 == 0 ? FoodType.NON_VEG : FoodType.VEG);
            food.setAvailable(true);
            food.setBestSeller(i % 7 == 0);
            food.setPopular(i % 5 == 0);
            food.setRating(Math.round((3.0 + ThreadLocalRandom.current().nextDouble() * 2) * 10.0) / 10.0);
            food.setCategory(category);
            food.setRestaurant(restaurant);
            foods.add(food);
        }
        foodItemRepository.saveAll(foods);
    }

    private void seedCustomers() {
        for (int i = 1; i <= 20; i++) {
            User customer = new User();
            customer.setName("Customer " + i);
            customer.setEmail("customer" + i + "@foodwings.com");
            customer.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            customer.setPhone("70000000" + String.format("%02d", i));
            customer.addRole(role(RoleName.CUSTOMER));
            userRepository.save(customer);
        }
    }

    private void seedDeliveryPartners() {
        for (int i = 1; i <= 10; i++) {
            User partner = new User();
            partner.setName("Delivery Partner " + i);
            partner.setEmail("delivery" + i + "@foodwings.com");
            partner.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            partner.setPhone("60000000" + String.format("%02d", i));
            partner.addRole(role(RoleName.DELIVERY_PARTNER));
            partner = userRepository.save(partner);

            DeliveryPartner profile = new DeliveryPartner();
            profile.setUser(partner);
            profile.setVehicleNumber("KA" + String.format("%02d", i) + "AB" + (1000 + i));
            profile.setCurrentLocation("City Center");
            profile.setAvailable(true);
            deliveryPartnerRepository.save(profile);
        }
    }

    private void seedCoupons() {
        String[] codes = {"WELCOME10", "FOODWINGS15", "SAVE20", "FLAT50", "PARTY25",
                "WEEKEND30", "FIRSTBITE", "TASTY12", "MEGA40", "HAPPY18"};
        for (int i = 0; i < codes.length; i++) {
            if (couponRepository.findByCodeIgnoreCase(codes[i]).isPresent()) {
                continue;
            }
            int percentage = 10 + (i % 6) * 5;
            Coupon coupon = new Coupon();
            coupon.setCode(codes[i]);
            coupon.setDescription(percentage + "% off on your order");
            coupon.setDiscountPercentage(BigDecimal.valueOf(percentage));
            coupon.setMinOrderAmount(BigDecimal.valueOf(199 + i * 20L));
            coupon.setMaxDiscount(BigDecimal.valueOf(100 + i * 25L));
            coupon.setExpiryDate(LocalDate.now().plusMonths(6));
            coupon.setActive(true);
            couponRepository.save(coupon);
        }
    }
}
