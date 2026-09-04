using BEVERLLY.Data;
using BEVERLLY.Extensions;
using BEVERLLY.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace BEVERLLY.Controllers;

public class CheckoutController : Controller
{
    private readonly BeverllyDbContext _db;
    public CheckoutController(BeverllyDbContext db)=>_db=db;

    public async Task<IActionResult> Index() {
        var items=HttpContext.Session.GetObject<List<CartItem>>("cart") ?? new();
        if(!items.Any()) return RedirectToAction("Index","Cart");
        var userId=HttpContext.Session.GetInt32("userId");
        var user=userId.HasValue?await _db.Users.FindAsync(userId.Value):null;
        return View(new CheckoutViewModel{Items=items,Address=user?.Address??""});
    }

    [HttpPost] public async Task<IActionResult> Place(CheckoutViewModel model) {
        var items=HttpContext.Session.GetObject<List<CartItem>>("cart") ?? new();
        if(!items.Any()) return RedirectToAction("Index","Cart");
        if(string.IsNullOrWhiteSpace(model.Address)){ModelState.AddModelError("Address","Please enter a delivery address."); return View("Index",new CheckoutViewModel{Items=items,Address=model.Address,PaymentMethod=model.PaymentMethod});}
        var order=new Order{OrderNumber="BEV-"+Random.Shared.Next(1000,9999),Date=DateTime.Now,Total=items.Sum(x=>x.LineTotal)+(items.Sum(x=>x.LineTotal)>=500?0:80),Status="Processing",DeliveryAddress=model.Address,UserId=HttpContext.Session.GetInt32("userId")};
        order.Items=items.Select(x=>new OrderItem{ProductId=x.ProductId,ProductName=x.ProductName,UnitPrice=x.Price,Quantity=x.Quantity,Size=x.Size}).ToList();
        _db.Orders.Add(order); await _db.SaveChangesAsync(); HttpContext.Session.Remove("cart");
        return RedirectToAction("Confirmation",new{id=order.Id});
    }
    public async Task<IActionResult> Confirmation(int id){var o=await _db.Orders.Include(x=>x.Items).FirstOrDefaultAsync(x=>x.Id==id); return o==null?NotFound():View(o);}
}
