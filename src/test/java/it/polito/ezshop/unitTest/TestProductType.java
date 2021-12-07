package it.polito.ezshop.unitTest;

import it.polito.ezshop.data.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestProductType {

    @Test
    public void testProductType() {
        ProductType p = new ProductTypeImpl(1, "milk", "8005235078697", 1.50, "dairy products" );
        assertNotNull(p);

        String product= p.toString();
        String expected= "ProductTypeImpl{" +
                "id=" + 1 +
                ", barcode='" + "8005235078697" + '\'' +
                ", description='" + "milk" + '\'' +
                ", sellPrice=" + 1.50 +
                ", quantity=" + 0 +
                ", notes='" + "dairy products" + '\'' +
                ", location='" + null + '\'' +
                '}';
        assertEquals(expected,product );

        //GET: id, description, barcode, sellPrice, notes
        int prodId = p.getId();
        assertEquals(1, prodId);

        String productCode = p.getBarCode();
        assertEquals("8005235078697", productCode);

        double pricePerUnit = p.getPricePerUnit();
        assertEquals(1.50, pricePerUnit, 0.00001);

        String notes = p.getNote();
        assertEquals("dairy products", notes);

        //SET & GET: quantity, location
        p.setQuantity(50);
        int quantity = p.getQuantity();
        assertEquals(50, quantity);

        String location = "12-A-2";
        p.setLocation(location);
        assertEquals("12-A-2", p.getLocation());

        //SET: id, description, barcode, sellPrice, notes
        p.setId(2);
        int pId=p.getId();
        assertEquals(2, pId);

        p.setProductDescription("pizza");
        assertEquals("pizza", p.getProductDescription());

        p.setBarCode("8711600786226");
        assertEquals("8711600786226", p.getBarCode());

        p.setPricePerUnit(2.9);
        assertEquals(2.9, p.getPricePerUnit(),0.00001);

        p.setNote("mozzarella cheese and tomatoes");
        assertEquals("mozzarella cheese and tomatoes", p.getNote());


    }
}
