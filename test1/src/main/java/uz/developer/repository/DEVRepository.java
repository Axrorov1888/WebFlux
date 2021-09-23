package uz.developer.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.developer.domain.DEV;

/**
 * Spring Data SQL repository for the DEV entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DEVRepository extends JpaRepository<DEV, Long> {}
