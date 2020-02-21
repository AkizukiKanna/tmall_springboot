package com.how2java.tmall.util;
  
import java.util.List;
 
import org.springframework.data.domain.Page;


/**
 * JPA 默认提供了一个分页类：org.springframework.data.domain.Page， 这个分页类已经能够满足各种分页需要了，所以大部分时候用它就足够了。
 * 但是除了一点，就是如图所示的，比如当前是第8页，前面要显示3个，后面要显示3个，总共7条分页点，这个类默认就不提供了。
 * 所以我们做了一个 Page4Navigator， 首先对 Page 类进行了封装，然后在构造方法里提供了一个 navigatePages 参数。
 * 这个参数如果传7，就表示如图所示的效果，前面3个，后面3个。
 *
 * 在构造方法里，还调用了 calcNavigatepageNums， 就是用来计算这个数值，并返回到一个int 数组变量 navigatepageNums ，方便前端遍历展示。
 */
public class Page4Navigator<T> {
    Page<T> pageFromJPA;
    int navigatePages;
      
    int totalPages;
  
    int number;
      
    long totalElements;
      
    int size;
  
    int numberOfElements;
  
    List<T> content;
  
    boolean isHasContent;
  
    boolean first;
  
    boolean last;
      
    boolean isHasNext;
  
    boolean isHasPrevious;
      
    int[] navigatepageNums;
      
    public Page4Navigator() {
        //这个空的分页是为了 Redis 从 json格式转换为 Page4Navigator 对象而专门提供的
    }
      
    public Page4Navigator(Page<T> pageFromJPA,int navigatePages) {
        this.pageFromJPA = pageFromJPA;
        this.navigatePages = navigatePages;
          
        totalPages = pageFromJPA.getTotalPages();
          
        number  = pageFromJPA.getNumber() ;
          
        totalElements = pageFromJPA.getTotalElements();
          
        size = pageFromJPA.getSize();
          
        numberOfElements = pageFromJPA.getNumberOfElements();
          
        content = pageFromJPA.getContent();
          
        isHasContent = pageFromJPA.hasContent();
                  
        first = pageFromJPA.isFirst();
          
        last = pageFromJPA.isLast();
          
        isHasNext = pageFromJPA.hasNext();
          
        isHasPrevious  = pageFromJPA.hasPrevious();      
          
        calcNavigatepageNums();
          
    }


    //分页导航个数处理方法
    private void calcNavigatepageNums() {
        int navigatepageNums[];
        int totalPages = getTotalPages();
        //num可能是指中间页
        int num = getNumber();
        //当总页数小于或等于导航页码数时
        if (totalPages <= navigatePages) {
            navigatepageNums = new int[totalPages];
            for (int i = 0; i < totalPages; i++) {
                navigatepageNums[i] = i + 1;
            }
        } else { //当总页数大于导航页码数时
            navigatepageNums = new int[navigatePages];
            int startNum = num - navigatePages / 2;
            int endNum = num + navigatePages / 2;
  
            if (startNum < 1) {
                startNum = 1;
                //(最前navigatePages页
                for (int i = 0; i < navigatePages; i++) {
                    navigatepageNums[i] = startNum++;
                }
            } else if (endNum > totalPages) {
                endNum = totalPages;
                //最后navigatePages页
                for (int i = navigatePages - 1; i >= 0; i--) {
                    navigatepageNums[i] = endNum--;
                }
            } else {
                //所有中间页
                for (int i = 0; i < navigatePages; i++) {
                    navigatepageNums[i] = startNum++;
                }
            }
        } 
        this.navigatepageNums = navigatepageNums;
    }
  
    public int getNavigatePages() {
        return navigatePages;
    }
  
    public void setNavigatePages(int navigatePages) {
        this.navigatePages = navigatePages;
    }
  
    public int getTotalPages() {
        return totalPages;
    }
  
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
  
    public int getNumber() {
        return number;
    }
  
    public void setNumber(int number) {
        this.number = number;
    }
  
    public long getTotalElements() {
        return totalElements;
    }
  
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
  
    public int getSize() {
        return size;
    }
  
    public void setSize(int size) {
        this.size = size;
    }
  
    public int getNumberOfElements() {
        return numberOfElements;
    }
  
    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }
  
    public List<T> getContent() {
        return content;
    }
  
    public void setContent(List<T> content) {
        this.content = content;
    }
  
    public boolean isHasContent() {
        return isHasContent;
    }
  
    public void setHasContent(boolean isHasContent) {
        this.isHasContent = isHasContent;
    }
  
    public boolean isFirst() {
        return first;
    }
  
    public void setFirst(boolean first) {
        this.first = first;
    }
  
    public boolean isLast() {
        return last;
    }
  
    public void setLast(boolean last) {
        this.last = last;
    }
  
    public boolean isHasNext() {
        return isHasNext;
    }
  
    public void setHasNext(boolean isHasNext) {
        this.isHasNext = isHasNext;
    }
  
    public boolean isHasPrevious() {
        return isHasPrevious;
    }
  
    public void setHasPrevious(boolean isHasPrevious) {
        this.isHasPrevious = isHasPrevious;
    }
  
    public int[] getNavigatepageNums() {
        return navigatepageNums;
    }
  
    public void setNavigatepageNums(int[] navigatepageNums) {
        this.navigatepageNums = navigatepageNums;
    }
  
}