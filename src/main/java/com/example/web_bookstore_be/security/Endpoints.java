package com.example.web_bookstore_be.security;

public class Endpoints {
    public static final String front_end_host = "http://localhost:3000";
    public static final String[] PUBLIC_GET_ENDPOINS = {
            "/images",
            "/genres/**",
            "/books",
            "/books/**",
            "review/search/countBy",
            "/feedback/totalFeedbacks",
            "/book/get-total",
            "/books/**",
            "/genre/**",
            "/review/**",
            "/images/**",
            "/images/**",
            "/users/search/existsByUsername/**",
            "/users/search/existsByEmail/**",
            "/users/search/existsByEmail",
            "users/search/existsByUsername",
            "/taikhoan/active-account/**",
            "/taikhoan/active-account",
            "/active-account",
            "active-account/**",
            "/active-account/**",
            "users/*/listCartItems",
            "/orders/**",
            "role/**",
            "/order-detail/*/book",
            "/order-details/**",
            "/users/*/listOrders",
            "/users/*/listRoles",
            "/users/*/listUsers",
            "/users/*",
            "/favorite-book/get-favorite-book/**",
            "/favorite-book/get-favorite-book/*",
            "favorite-book//get-favorite-book",
            "/users/*/listFavoriteBooks",
            "/favorite-book/*/book",
            "/vnpay/**",
            "/cart-item/**",
            "/cart-item/get-all",
            "/cart-items/*/book",
            "/taikhoan/forgot-password",


    };

    public static final String[] PUBLIC_POST_ENDPOINS = {
            "/taikhoan/register",
            "taikhoan/register/**",
            "taikhoan/register/*",
            "/taikhoan/authenticate",
            "feedback/add-feedback",
            "/cart-item/add-item",
            "taikhoan/authenticate",
            "taikhoan/**",
            "/orders/**",
            "review/**",
            "/reviews/add-review/**",
            "/feedback/add-feedback",
            "/favorite-book/add-book",
            "/vnpay/create-payment/**",
            "/reviews/get-review/**",
            "cart-item/add-item",
            "/cart-item/add-item/**",
            "taikhoan/add-user",
    };


    public static final String[] PUBLIC_PUT_ENDPOINS = {
            "/cart-item/**",
            "/taikhoan/forgot-password",
            "/taikhoan/update-profile",
            "/user/**",
            "/taikhoan/change-avatar",
            "/user/update-profile",
            "/user/change-password",
            "/user/forgot-password",
            "/user/change-avatar",
            "/orders/update-order",
            "/orders/cancel-order",
            "/reviews/update-review",
            "/cart-item/update-item",
            "/taikhoan/update-user",
            "/genres/**",
    };
    public static final String[] PUBLIC_DELETE_ENDPOINS = {
            "/cart-items/**",
            "/favorite-books/delete-book",
            "/favorite-book/**",
            "/cart-item/**",
            "/cart-item/delete-item",
            "/cart-item/delete-item/**",
            "/genres/**",
    };
    public static final String[] ADMIN_ENDPOINS = {
            "/books",
            "/books/**",
            "/users",
            "/users/**",
            "/favorite-book/get-favorite-book/**",
            "/favorite-book/get-favorite-book/*",
            "favorite-book/get-favorite-book",
            "cart-items/add-item",
            "/cart-item/**",
            "/books/add-book/**",
            "/taikhoan/add-user/**",
            "/feedbacks/**",
            "/cart-item/**",
            "/cart-item/**",
            "/orders/**",
            "/orders/**",
            "/order-details/**",
            "/roles/**",
            "/favorite-book/**",
            "/favorite-book/add-book",
            "/cart-item/**",
            "/taikhoan/update-profile",
            "/taikhoan/change-avatar",
            "feedback/add-feedback",
            "/taikhoan/forgot-password",
            "/**",

    };







}
