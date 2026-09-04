using BEVERLLY.Data;
using BEVERLLY.Extensions;
using BEVERLLY.Models;
using Microsoft.AspNetCore.Mvc;

namespace BEVERLLY.Controllers;

public class CartController : Controller
{
    private readonly BeverllyDbContext _db;
    public CartController(BeverllyDbContext db) => _db=db;

    private List<CartItem> GetCart() => HttpContext.Session.GetObject<List<CartItem>>("cart") ?? new();
    private void Save(List<CartItem> cart) => HttpContext.Session.SetObject("cart", cart);

    public IActionResult Index() => View(GetCart());

    [HttpPost] public IActionResult Add(int productId, string size, int quantity=1)
    {
        var product = _db.Products.Find(productId);
        if (product == null) return NotFound();
        var cart=GetCart(); var item=cart.FirstOrDefault(x=>x.ProductId==productId && x.Size==size);
        if(item!=null) item.Quantity += quantity;
        else cart.Add(new CartItem{ProductId=product.Id,ProductName=product.Name,Price=product.Price,Image=product.Image,Quantity=quantity,Size=size});
        Save(cart); return RedirectToAction("Index");
    }
    [HttpPost] public IActionResult Remove(int productId, string size) { var cart=GetCart(); cart.RemoveAll(x=>x.ProductId==productId && x.Size==size); Save(cart); return RedirectToAction("Index"); }
    [HttpPost] public IActionResult Update(int productId,string size,int quantity) { var cart=GetCart(); var i=cart.FirstOrDefault(x=>x.ProductId==productId&&x.Size==size); if(i!=null)i.Quantity=Math.Max(1,quantity); Save(cart); return RedirectToAction("Index"); }

    public IActionResult Wishlist() {
        var ids=HttpContext.Session.GetObject<List<int>>("wishlist") ?? new();
        ViewBag.Products=_db.Products.Where(p=>ids.Contains(p.Id)).ToList();
        return View();
    }
    [HttpPost] public IActionResult ToggleWishlist(int productId) {
        var ids=HttpContext.Session.GetObject<List<int>>("wishlist") ?? new();
        if(ids.Contains(productId)) ids.Remove(productId); else ids.Add(productId);
        HttpContext.Session.SetObject("wishlist",ids);
        return RedirectToAction("Details","Shop",new{id=productId});
    }
}
