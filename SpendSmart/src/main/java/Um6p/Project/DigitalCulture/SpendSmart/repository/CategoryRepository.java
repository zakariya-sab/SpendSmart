package Um6p.Project.DigitalCulture.SpendSmart.repository;

import Um6p.Project.DigitalCulture.SpendSmart.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Category entity database operations.
 * Extends JpaRepository to inherit standard CRUD and pagination methods.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find a category by its name.
     * Used to prevent duplicate category creation.
     *
     * @param name the category name to search for
     * @return an Optional containing the category if found, or empty if not found
     */
    Optional<Category> findByName(String name);

    /**
     * Check if a category with the given name already exists.
     * Used to validate uniqueness before creating a new category.
     *
     * @param name the category name to check
     * @return true if a category with this name exists, false otherwise
     */
    boolean existsByName(String name);
}
