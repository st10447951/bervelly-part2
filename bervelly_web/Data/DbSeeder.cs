using BEVERLLY.Models;

namespace BEVERLLY.Data;

public static class DbSeeder
{
    public static void Seed(BeverllyDbContext db)
    {
        if (!db.Products.Any())
        {
            db.Products.AddRange(
                new Product { Id=1, Name="BEVERLLY Classic Tee — White", Category="Casual", Price=350, Description="Oversized white tee with the iconic BEVERLLY South Africa logo and double monogram detail on the hem. 100% heavyweight cotton. Shot to perfection.", Sizes="XS,S,M,L,XL", Stock=12, IsNew=true, Image="product_tee_white.png" },
                new Product { Id=2, Name="BEVERLLY Classic Tee — Black", Category="Casual", Price=350, Description="Oversized black tee with BEVERLLY South Africa logo and double monogram detail on the hem. Worn against the Pretoria skyline — iconic.", Sizes="XS,S,M,L,XL", Stock=10, IsNew=true, Image="product_tee_black.png" },
                new Product { Id=3, Name="Snake Tee — Black/Red", Category="Streetwear", Price=450, Description="Statement oversized tee with bold red BEVERLLY lettering and graphic serpent print. A BEVERLLY signature piece — the one that defines the brand.", Sizes="XS,S,M,L,XL,XXL", Stock=8, IsNew=true, Image="product_tee_snake.png" },
                new Product { Id=4, Name="Snake Hoodie — Black/Red", Category="Streetwear", Price=899, Description="Heavyweight oversized hoodie with red BEVERLLY South Africa lettering and serpent graphic on the back. Brushed fleece interior.", Sizes="S,M,L,XL,XXL", Stock=15, IsNew=false, Image="product_hoodie_red.png" },
                new Product { Id=5, Name="Wrap Logo Hoodie — Black", Category="Streetwear", Price=899, Description="Oversized black hoodie with the BEVERLLY wordmark wrapping across the back and sleeves in white. Bold, unmissable, effortlessly street.", Sizes="S,M,L,XL,XXL", Stock=18, IsNew=false, Image="product_hoodie_wrap.png" },
                new Product { Id=6, Name="Snake Hoodie — Black/White", Category="Streetwear", Price=899, Description="The original BEVERLLY snake hoodie in classic black and white. White lettering, serpent graphic. The piece that started it all.", Sizes="S,M,L,XL,XXL", Stock=7, IsNew=false, Image="product_hoodie_bw.png" }
            );
            db.SaveChanges();
        }
        if (!db.Users.Any())
        {
            db.Users.Add(new User { FirstName="Thabo", LastName="Customer", Email="thabo@email.co.za", PasswordHash="password123", Phone="", Address="" });
            db.SaveChanges();
        }
    }
}
