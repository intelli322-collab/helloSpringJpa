package kr.ac.hansung.cse.repository;

import kr.ac.hansung.cse.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepository {

    // PPT 5-2_JPA: @PersistenceContext는 Repository에만 위치
    @PersistenceContext
    private EntityManager em;

    public List<Category> findAll() {
        return em.createQuery("SELECT c FROM Category c ORDER BY c.name", Category.class)
                .getResultList();
    }

    public Category save(Category category) {
        if (category.getId() == null) {
            em.persist(category);
            return category;
        }

        return em.merge(category);
    }

    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(em.find(Category.class, id));
    }

    public Optional<Category> findByName(String name) {
        List<Category> result = em.createQuery(
                        "SELECT c FROM Category c WHERE c.name = :name", Category.class)
                .setParameter("name", name)   // Named parameter → SQL Injection 방지
                .getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public long countProductsByCategoryId(Long categoryId) {
        return em.createQuery(
                        "SELECT COUNT(p) FROM Product p WHERE p.category.id = :id", Long.class)
                .setParameter("id", categoryId)
                .getSingleResult();  // COUNT는 항상 1개 결과 → getSingleResult() 사용
    }

    public void delete(Long id) {
        Category c = em.find(Category.class, id);
        if (c != null) {
            em.remove(c);  // Managed → Removed → 트랜잭션 종료 시 DELETE SQL 실행
        }
    }
}