using BEVERLLY.Data;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace BEVERLLY.Controllers;

public class HomeController : Controller
{
    private readonly BeverllyDbContext _db;
    public HomeController(BeverllyDbContext db) => _db = db;

    public async Task<IActionResult> Index()
    {
        var products = await _db.Products.ToListAsync();
        ViewBag.NewArrivals = products.Where(p => p.IsNew).ToList();
        ViewBag.Trending = products.Where(p => !p.IsNew).ToList();
        return View();
    }
}
