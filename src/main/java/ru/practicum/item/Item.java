package ru.practicum.item;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import ru.practicum.user.User;
import jakarta.persistence.*;
import ru.practicum.item.Comment;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items", schema = "public")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "available", nullable = false)
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "request_id")
    private Long requestId;

    @OneToMany(mappedBy = "item")
    private List<Comment> comments = new ArrayList<>();
}
