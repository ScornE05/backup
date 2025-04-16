package com.example.demo.Controller;


import com.example.demo.DTO.UserDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Pet;
import com.example.demo.entity.Product;
import com.example.demo.service.CategoryService;
import com.example.demo.service.PetService;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WebController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final PetService petService;

    @Autowired
    public WebController(ProductService productService, CategoryService categoryService, PetService petService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.petService = petService;
    }

    @GetMapping("/")
    public String index(Model model) {
        // Get featured products
        List<Product> featuredProducts = productService.getFeaturedProducts();
        model.addAttribute("featuredProducts", featuredProducts);

        // Get top categories
        List<Category> parentCategories = categoryService.getAllParentCategories();
        model.addAttribute("categories", parentCategories);

        // Get available pets
        Pageable pageable = PageRequest.of(0, 6, Sort.by("createdAt").descending());
        Page<Pet> availablePets = petService.getAvailablePets(pageable);
        model.addAttribute("pets", availablePets.getContent());

        // Add empty user for login form
        model.addAttribute("user", new UserDTO());

        return "index";
    }

    @GetMapping("/products")
    public String products(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> products;

        if (categoryId != null) {
            products = productService.getActiveProductsByCategoryId(categoryId, pageable);
            model.addAttribute("currentCategory", categoryService.getCategoryById(categoryId).orElse(null));
        } else if (search != null && !search.trim().isEmpty()) {
            products = productService.searchProductsByName(search, pageable);
            model.addAttribute("searchTerm", search);
        } else {
            products = productService.getAllProducts(pageable);
        }

        model.addAttribute("products", products);

        // Get all categories for sidebar
        List<Category> categories = categoryService.getAllParentCategories();
        model.addAttribute("categories", categories);

        return "products";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        model.addAttribute("product", product);

        // Get related products from same category
        Pageable pageable = PageRequest.of(0, 4);
        Page<Product> relatedProducts = productService.getActiveProductsByCategoryId(product.getCategory().getId(), pageable);
        model.addAttribute("relatedProducts", relatedProducts.getContent());

        return "product-detail";
    }

    @GetMapping("/pets")
    public String pets(
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String breed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Pet> pets;

        if (species != null && breed != null) {
            pets = petService.getAvailablePetsBySpeciesAndBreed(species, breed, pageable);
            model.addAttribute("currentSpecies", species);
            model.addAttribute("currentBreed", breed);
        } else if (species != null) {
            pets = petService.getAvailablePetsBySpecies(species, pageable);
            model.addAttribute("currentSpecies", species);
        } else {
            pets = petService.getAvailablePets(pageable);
        }

        model.addAttribute("pets", pets);

        // Get all species for filter
        List<String> allSpecies = petService.getAllSpecies();
        model.addAttribute("species", allSpecies);

        // Get breeds for selected species
        if (species != null) {
            List<String> breeds = petService.getBreedsBySpecies(species);
            model.addAttribute("breeds", breeds);
        }

        return "pets";
    }

    @GetMapping("/pets/{id}")
    public String petDetail(@PathVariable Long id, Model model) {
        Pet pet = petService.getPetById(id)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        model.addAttribute("pet", pet);

        // Get similar pets
        Pageable pageable = PageRequest.of(0, 4);
        Page<Pet> similarPets = petService.getAvailablePetsBySpecies(pet.getSpecies(), pageable);
        model.addAttribute("similarPets", similarPets.getContent());

        return "pet-detail";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new UserDTO());
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @GetMapping("/cart")
    public String cart() {
        return "cart";
    }

    @GetMapping("/checkout")
    public String checkout() {
        return "checkout";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/orders")
    public String orders() {
        return "orders";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
}
