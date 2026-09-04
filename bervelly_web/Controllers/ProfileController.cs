using BEVERLLY.Data;
using Microsoft.AspNetCore.Mvc;

namespace BEVERLLY.Controllers;
public class ProfileController : Controller
{
    private readonly BeverllyDbContext _db;
    public ProfileController(BeverllyDbContext db)=>_db=db;
    public async Task<IActionResult> Index(){var id=HttpContext.Session.GetInt32("userId"); if(!id.HasValue)return RedirectToAction("Login","Account"); var u=await _db.Users.FindAsync(id); return View(u);}
    [HttpPost] public async Task<IActionResult> Update(Models.User model){var id=HttpContext.Session.GetInt32("userId"); if(!id.HasValue)return RedirectToAction("Login","Account"); var u=await _db.Users.FindAsync(id); if(u==null)return NotFound(); u.FirstName=model.FirstName;u.LastName=model.LastName;u.Phone=model.Phone;u.Address=model.Address; await _db.SaveChangesAsync(); TempData["Message"]="Profile updated"; return RedirectToAction("Index");}
}
