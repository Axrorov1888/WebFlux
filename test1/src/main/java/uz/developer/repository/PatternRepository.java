package uz.developer.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.developer.domain.Pattern;

/**
 * Spring Data SQL repository for the Pattern entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PatternRepository extends JpaRepository<Pattern, Long> {}
