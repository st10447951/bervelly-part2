# BEVERLLY — ASP.NET Core MVC

This is a web conversion of the supplied BEVERLLY Android Studio/Kotlin project.

## Converted features
- Login and registration
- Home page with hero, New Arrivals and Trending
- Product catalogue with search and category filters
- Product details, size selection and quantity
- Session-based cart
- Wishlist
- Checkout with delivery address and payment method
- Order confirmation and order history
- Profile editing
- SQLite database recreated from the Android product/user/order models
- Original BEVERLLY logo and product/hero images copied from the Android project

## Open in Visual Studio
1. Extract the ZIP.
2. Open `BEVERLLY.sln` (or open `BEVERLLY.csproj`).
3. Restore NuGet packages.
4. Press **F5**.
5. The SQLite database is automatically created in `Data/beverlly.db`.

### Demo login
Email: `thabo@email.co.za`
Password: `password123`

## Important database note
The Android project supplied does not contain a Firebase configuration or an external SQL database. Its catalogue and cart are implemented in Kotlin repositories and its user model uses Room/local storage. The MVC version therefore recreates the same application data model in SQLite using Entity Framework Core. If you have a separate MySQL/SQL Server database you want to keep instead, replace the EF provider/connection string and map the existing tables.

## Visual Studio
Recommended: Visual Studio 2022/2026 with the ASP.NET and web development workload and .NET 8 SDK.
