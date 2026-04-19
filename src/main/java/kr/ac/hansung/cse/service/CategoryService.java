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

    // PPT 5-2_JPA: "final + 생성자 주입 (권장)"
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

    // ── 쓰기 (@Transactional 오버라이드 → readOnly = false) ───

    // PPT 5-2_JPA: "@Transactional 오버라이드 (readOnly = false)"
    @Transactional
    public Category createCategory(String name) {
        // 비즈니스 규칙: 이름 중복 검사
        // ifPresent → Optional에 값이 있을 때만 실행 (람다)
        categoryRepository.findByName(name)
                .ifPresent(existing -> {
                    throw new DuplicateCategoryException(name);
                });

        // PPT 5-2_JPA: persist() → INSERT SQL 자동 실행
        return categoryRepository.save(new Category(name));
    }

    @Transactional
    public void deleteCategory(Long id) {
        // 삭제 전 연결 상품 수 확인
        // getProducts().size() 대신 COUNT 쿼리 → LAZY 로딩 방지, 성능 우수
        long count = categoryRepository.countProductsByCategoryId(id);
        if (count > 0) {
            throw new IllegalStateException(
                    "상품 " + count + "개가 연결되어 있어 삭제할 수 없습니다."
            );
        }
        categoryRepository.delete(id);
    }
}