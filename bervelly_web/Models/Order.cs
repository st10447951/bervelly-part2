namespace BEVERLLY.Models;

public class Order
{
    public int Id { get; set; }
    public string OrderNumber { get; set; } = "";
    public DateTime Date { get; set; }
    public decimal Total { get; set; }
    public string Status { get; set; } = "Processing";
    public string DeliveryAddress { get; set; } = "";
    public int? UserId { get; set; }
    public List<OrderItem> Items { get; set; } = new();
}

public class OrderItem
{
    public int Id { get; set; }
    public int OrderId { get; set; }
    public int ProductId { get; set; }
    public string ProductName { get; set; } = "";
    public decimal UnitPrice { get; set; }
    public int Quantity { get; set; }
    public string Size { get; set; } = "";
    public Order? Order { get; set; }
}
