namespace BEVERLLY.Models;

public class Product
{
    public int Id { get; set; }
    public string Name { get; set; } = "";
    public string Category { get; set; } = "";
    public decimal Price { get; set; }
    public string Description { get; set; } = "";
    public string Sizes { get; set; } = "";
    public int Stock { get; set; }
    public bool IsNew { get; set; }
    public string Image { get; set; } = "";
    public List<string> SizeList => Sizes.Split(',', StringSplitOptions.RemoveEmptyEntries).ToList();
}
