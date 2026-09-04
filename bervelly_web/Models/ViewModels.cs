namespace BEVERLLY.Models;

public class LoginViewModel
{
    public string Email { get; set; } = "";
    public string Password { get; set; } = "";
}
public class RegisterViewModel
{
    public string FirstName { get; set; } = "";
    public string LastName { get; set; } = "";
    public string Email { get; set; } = "";
    public string Password { get; set; } = "";
    public string Phone { get; set; } = "";
    public string Address { get; set; } = "";
}
public class ShopViewModel
{
    public List<Product> Products { get; set; } = new();
    public string Query { get; set; } = "";
    public string Category { get; set; } = "All";
    public List<string> Categories { get; set; } = new();
}
public class ProductDetailViewModel
{
    public Product Product { get; set; } = null!;
    public bool InWishlist { get; set; }
}
public class CheckoutViewModel
{
    public List<CartItem> Items { get; set; } = new();
    public string Address { get; set; } = "";
    public string PaymentMethod { get; set; } = "EFT";
    public decimal Subtotal => Items.Sum(x => x.LineTotal);
    public decimal Delivery => Subtotal >= 500 ? 0 : 80;
    public decimal Total => Subtotal + Delivery;
}
