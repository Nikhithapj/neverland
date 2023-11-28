package com.example.library.repository;

import com.example.library.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findById(long id);
    @Query("select p from Product p where p.is_deleted = false and p.is_activated = true and p.category.deleted=false and p.category.activated=true")
    List<Product> getAllProduct();

    @Query("select p from Product p where p.name like %?1% or p.description like %?1%")
    List<Product> findAllByNameOrDescription(String keyword);


    @Query("select p from Product p inner join Category c ON c.id = p.category.id" +
            " where p.category.name = ?1 and p.is_activated = true and p.is_deleted = false")
    List<Product> findAllByCategory(String category);

//    @Query("select p from Product p inner join Category c ON c.id = p.category.id" +
//            " where p.category.name = ?1 and p.is_activated = true and p.is_deleted = false and c.activated=true ")
//    List<Product> findAllByCategoryAnd_activated(String category);

    @Query(value = "select " +
            "p.product_id, p.name, p.description, p.current_quantity, p.cost_price, p.category_id, p.sale_price, p.image, p.is_activated, p.is_deleted " +
            "from products p where p.is_activated = true and p.is_deleted = false order by rand() limit 9", nativeQuery = true)
    List<Product> randomProduct();

    @Query(value = "select " +
            "p.product_id, p.name, p.description, p.current_quantity, p.cost_price, p.category_id, p.sale_price, p.image, p.is_activated, p.is_deleted " +
            "from products p where p.is_deleted = false and p.is_activated = true order by p.cost_price desc limit 9", nativeQuery = true)
    List<Product> filterHighProducts();

    @Query(value = "select " +
            "p.product_id, p.name, p.description, p.current_quantity, p.cost_price, p.category_id, p.sale_price, p.image, p.is_activated, p.is_deleted " +
            "from products p where p.is_deleted = false and p.is_activated = true order by p.cost_price asc limit 9", nativeQuery = true)
    List<Product> filterLowerProducts();


    @Query(value = "select p.product_id, p.name, p.description, p.current_quantity, p.cost_price, p.category_id, p.sale_price, p.image, p.is_activated, p.is_deleted from products p where p.is_deleted = false and p.is_activated = true limit 4", nativeQuery = true)
    List<Product> listViewProduct();


    @Query(value = "select p from Product p inner join Category c on c.id = ?1 and p.category.id = ?1 where p.is_activated = true and p.is_deleted = false")
    List<Product> getProductByCategoryId(Long id);


    @Query("select p from Product p where p.name like %?1% or p.description like %?1%")
    List<Product> searchProducts(String keyword);


    @Query("select p from Product p where p.category.id = :id")
    List<Product> findBYCategoryId(@Param("id")Long id);

    List<Product> findAllByCategoryId(Long id);

//    @Query(value = "SELECT * FROM products WHERE products.product_activated = true", nativeQuery = true)
//    List<Product> findAllByActivatedTrue();
//

@Query(value="SELECT p from Product p where p.is_activated = true")
List<Product> findAllByActivatedTrue();

    List<Product> findAll();


@Query("SELECT p.name,SUM(p.currentQuantity)FROM Product  p  group by p.name")
List<Object[]>getTotalQuantityPerProduct();

}
