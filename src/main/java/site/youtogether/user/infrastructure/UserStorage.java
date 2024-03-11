package site.youtogether.user.infrastructure;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import site.youtogether.user.User;

@Repository
public interface UserStorage extends CrudRepository<User, String> {
	
}
