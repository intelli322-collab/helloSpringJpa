package kr.ac.hansung.cse.exception;

public class DuplicateCategoryException extends RuntimeException {

    private final String categoryName;

    public DuplicateCategoryException(String name) {
        super("이미 존재하는 카테고리입니다: " + name);
        this.categoryName = name;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
