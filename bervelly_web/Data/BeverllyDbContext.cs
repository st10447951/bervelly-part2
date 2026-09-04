using BEVERLLY.Models;
using Microsoft.EntityFrameworkCore;

namespace BEVERLLY.Data;

public class BeverllyDbContext : DbContext
{
    public BeverllyDbContext(DbContextOptions<BeverllyDbContext> options) : base(options) { }
    public DbSet<Product> Products => Set<Product>();
    public DbSet<User> Users => Set<User>();
    public DbSet<Order> Orders => Set<Order>();
    public DbSet<OrderItem> OrderItems => Set<OrderItem>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<User>().HasIndex(x => x.Email).IsUnique();
        modelBuilder.Entity<OrderItem>().HasOne(x => x.Order).WithMany(x => x.Items)
            .HasForeignKey(x => x.OrderId).OnDelete(DeleteBehavior.Cascade);
    }
}
