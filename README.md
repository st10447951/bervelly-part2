# bervelly

# BEVERLLY – Clothing E-Commerce Platform

BEVERLLY is a clothing e-commerce platform consisting of an **Android mobile application** and an **ASP.NET Core MVC web application**. Both applications provide customers with a consistent shopping experience.

## Applications

### Android App

Developed using:

* Kotlin
* Android Studio
* XML
* Room Database
* SharedPreferences
* Material Design

###  Web Application

Developed using:

* C#
* ASP.NET Core MVC
* Razor Views
* Entity Framework Core
* HTML, CSS & JavaScript
* Bootstrap

## Features

* User registration and login
* Browse clothing products
* Product categories and search
* Product details and images
* Shopping cart
* Wishlist
* Checkout
* Order management
* Customer profile
* Delivery address management

### Delivery

| Order        | Delivery Fee |
| ------------ | -----------: |
| R500 or more |         FREE |
| Below R500   |          R80 |

## Repository Structure

```text
BEVERLLY/
│
├── README.md
│
├── BEVERLLY-Android/
│   ├── app/
│   └── ...
│
├── BEVERLLY-Web/
│   ├── Controllers/
│   ├── Models/
│   ├── Views/
│   ├── wwwroot/
│   └── ...
│
└── .gitignore
```

## Database

The Android application uses **Room Database**, while the web application uses **Entity Framework Core**.

The main data includes:

* Users
* Products
* Cart Items
* Wishlists
* Orders

## Getting Started

### Android

1. Open `BervellyApp` in Android Studio.
2. Allow Gradle to sync.
3. Connect an Android device or start an emulator.
4. Click **Run**.

### Web

1. Open project `BERVELLY.sln` in Visual Studio.
2. Restore the NuGet packages.
3. Run the application.
```

## Project Goal

The goal of BEVERLLY is to provide a modern clothing shopping platform across **mobile and web**, while maintaining a consistent brand, product catalogue and customer experience.

## License

This project was developed for educational purposes.

© 2026 BEVERLLY. All rights reserved.
