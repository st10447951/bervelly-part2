using BEVERLLY.Data;
using BEVERLLY.Extensions;
using BEVERLLY.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace BEVERLLY.Controllers;

public class ShopController : Controller
{
    private readonly BeverllyDbContext _db;
    public ShopController(BeverllyDbContext db) => _db = db;

    public async Task<IActionResult> Index(string query="", string category="All")
    {
        var products = _db.Products.AsQueryable();
        if (!string.IsNullOrWhiteSpace(query))
            products = products.Where(p => p.Name.Contains(query) || p.Category.Contains(query));
        if (category != "All") products = products.Where(p => p.Category == category);
        return View(new ShopViewModel {
            Products = await products.ToListAsync(), Query=query, Category=category,
            Categories = new List<string>{"All","Casual","Streetwear"}
        });
    }

    public async Task<IActionResult> Details(int id)
    {
        var product = await _db.Products.FindAsync(id);
        if (product == null) return NotFound();
        var wishlist = HttpContext.Session.GetObject<List<int>>("wishlist") ?? new();
        return View(new ProductDetailViewModel { Product=product, InWishlist=wishlist.Contains(id) });
    }
}
