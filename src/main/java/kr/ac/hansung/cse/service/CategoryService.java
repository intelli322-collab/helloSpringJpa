package kr.ac.hansung.cse.service;

import kr.ac.hansung.cse.exception.DuplicateCategoryException;
import kr.ac.hansung.cse.model.Category;
import kr.ac.hansung.cse.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// PPT 5-2_JPA: "클래스 기본: 읽기 전용 — Dirty Checking 비활성화 → 성능 향상"
@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // ── 읽기 (readOnly = true 상속) ───────────────────────────

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리: " + id));
    }

    // readOnly 오버라이드 → 쓰기 허용
    @Transactional
    public Category createCategory(String name) {
        // 중복 검사: 이름이 이미 있으면 예외 발생
        categoryRepository.findByName(name)
                .ifPresent(existing -> {
                    throw new DuplicateCategoryException(name);
                });

        return categoryRepository.save(new Category(name));
    }

    @Transactional
    public void deleteCategory(Long id) {
        long count = categoryRepository.countProductsByCategoryId(id);
        if (count > 0) {
            throw new IllegalStateException(
                    "상품 " + count + "개가 연결되어 있어 삭제할 수 없습니다."
            );
        }
        categoryRepository.delete(id);
    }
}