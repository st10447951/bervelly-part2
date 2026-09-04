using BEVERLLY.Data;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace BEVERLLY.Controllers;
public class OrdersController : Controller
{
    private readonly BeverllyDbContext _db;
    public OrdersController(BeverllyDbContext db)=>_db=db;
    public async Task<IActionResult> Index()
    {
        var uid = HttpContext.Session.GetInt32("userId"); var q = _db.Orders.Include(x => x.Items).OrderByDescending(x => x.Date); if (uid.HasValue)
            q = (IOrderedQueryable<Models.Order>)q.Where(x => x.UserId == uid); return View(await q.ToListAsync());
    }
}
