package com.sparta.shoppingmall.domain.user.entity;

import com.sparta.shoppingmall.common.base.entity.Timestamped;
import com.sparta.shoppingmall.domain.cart.entity.Cart;
import com.sparta.shoppingmall.domain.comment.entity.Comment;
import com.sparta.shoppingmall.domain.follows.entity.Follows;
import com.sparta.shoppingmall.domain.like.entity.Likes;
import com.sparta.shoppingmall.domain.order.entity.OrderGroup;
import com.sparta.shoppingmall.domain.product.entity.Product;
import com.sparta.shoppingmall.domain.user.dto.SignupRequest;
import com.sparta.shoppingmall.domain.user.dto.AdminUpdateUserRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String recentPassword;

    @Column
    private String recentPassword2;

    @Column
    private String recentPassword3;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String address;

    @Column(columnDefinition = "varchar(30) default 'USER'", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserType userType;   // UserRole

    @Column(columnDefinition = "varchar(30)")
    @Enumerated(value = EnumType.STRING)
    private UserStatus userStatus;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Comment> comments = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderGroup> orderGroups = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Likes> likes = new ArrayList<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follows> followings; //내가 팔로잉 하는 리스트

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follows> followers; //나를 팔로우 하는 리스트

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @Builder
    public User(String username, String password, String name, String email, String address, UserStatus userStatus, UserType userType) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.address = address;
        this.userStatus = userStatus;
        this.userType = userType;
    }

    /**
     * 사용자 생성
     */
    private static User createUser(final SignupRequest request, String password, UserType userType) {
        return User.builder()
                .username(request.getUsername())
                .password(password)
                .name(request.getName())
                .email(request.getEmail())
                .address(request.getAddress())
                .userStatus(UserStatus.JOIN)
                .userType(userType)
                .build();
    }

    /**
     * 사용자 생성과 동시에 장바구니 생성
     */
    public static User createUserWithCart(final SignupRequest request, String password, UserType userType){
        User user = createUser(request, password, userType);
        Cart cart = Cart.createCart(user);
        user.setCart(cart);
        return user;
    }

    /**
     * 프로필 수정
     */
    public void editProfile(String name, String email, String address) {
        this.name = name;
        this.address = address;
        this.email = email;
    }

    /**
     * 비밀번호 변경
     */
    public void changePassword(String newPassword) {
        this.recentPassword3 = this.recentPassword2;
        this.recentPassword2 = this.recentPassword;
        this.recentPassword = this.password;
        this.password = newPassword;
    }

    /**
     * 회원 상태변경
     */
    public void changeStatus(UserStatus status) {
        this.userStatus = status;
    }

    /**
     * 관리자 - 회원 정보 수정
     */
    public void adminUpdateUser(AdminUpdateUserRequest request) {
        this.username = request.getUsername();
        this.password = request.getPassword();
        this.recentPassword = request.getRecentPassword();
        this.recentPassword2 = request.getRecentPassword2();
        this.recentPassword3 = request.getRecentPassword3();
        this.name = request.getName();
        this.email = request.getEmail();
        this.address = request.getAddress();
        this.userType = request.getUserType();
        this.userStatus = request.getUserStatus();
    }
}
