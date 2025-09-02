package br.com.robson.robssohex.entities;

import br.com.robson.robssohex.HttpMethodEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HttpMethodEnum method;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String resource;

    private String path;

    private String openapi;

    @Column(length = 1024)
    private String curlExample;
}