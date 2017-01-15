package jjug.speaker;

import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@DynamicUpdate
public class Speaker {
	@Id
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@GeneratedValue(generator = "uuid")
	@Column(columnDefinition = "binary(16)")
	private UUID speakerId;
	@NotEmpty
	@Size(max = 255)
	private String name;
	@NotEmpty
	@Size(max = 255)
	private String companyOrCommunity;
	@Lob
	@NotEmpty
	@Size(max = 5120)
	private String bio;
	@Lob
	@NotEmpty
	@Size(max = 5120)
	private String activities;
	@Column(unique = true)
	@NotEmpty
	@Size(max = 255)
	private String github;
	@NotEmpty
	@Size(max = 255)
	private String profileUrl;
	@NotEmpty
	@Email
	@Size(max = 255)
	private String email;
	private boolean transportationAllowance = false;
	@Size(max = 255)
	private String city;
	@Lob
	@Size(max = 5120)
	private String note;
}
