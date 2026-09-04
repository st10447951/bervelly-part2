using BEVERLLY.Data;
using BEVERLLY.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace BEVERLLY.Controllers;

public class AccountController : Controller
{
    private readonly BeverllyDbContext _db;
    public AccountController(BeverllyDbContext db) => _db = db;

    [HttpGet] public IActionResult Login(string? returnUrl=null) { ViewBag.ReturnUrl=returnUrl; return View(); }

    [HttpPost] public async Task<IActionResult> Login(LoginViewModel model, string? returnUrl=null)
    {
        if (!ModelState.IsValid) return View(model);
        var user = await _db.Users.FirstOrDefaultAsync(x => x.Email == model.Email && x.PasswordHash == model.Password);
        if (user == null) { ModelState.AddModelError("", "Invalid email or password."); return View(model); }
        HttpContext.Session.SetInt32("userId", user.Id);
        HttpContext.Session.SetString("userName", user.FirstName);
        return Redirect(returnUrl ?? Url.Action("Index","Home")!);
    }

    [HttpGet] public IActionResult Register() => View();

    [HttpPost] public async Task<IActionResult> Register(RegisterViewModel model)
    {
        if (await _db.Users.AnyAsync(x=>x.Email==model.Email)) { ModelState.AddModelError("Email","An account with this email already exists."); return View(model); }
        var user = new User { FirstName=model.FirstName, LastName=model.LastName, Email=model.Email, PasswordHash=model.Password, Phone=model.Phone, Address=model.Address };
        _db.Users.Add(user); await _db.SaveChangesAsync();
        HttpContext.Session.SetInt32("userId", user.Id); HttpContext.Session.SetString("userName", user.FirstName);
        return RedirectToAction("Index","Home");
    }

    public IActionResult Logout() { HttpContext.Session.Clear(); return RedirectToAction("Login"); }
}
