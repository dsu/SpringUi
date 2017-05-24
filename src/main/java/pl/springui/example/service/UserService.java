package pl.springui.example.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pl.springui.components.list.PagedDataService;
import pl.springui.example.model.User;

@Service
public class UserService implements PagedDataService<User> {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected List<User> list = new ArrayList<>();

	{
		list.add(new User(1, "Jan", 31));
		list.add(new User(2, "Andrzej", 41));
		list.add(new User(3, "Arek", 40));
		list.add(new User(4, "Staszek", 38));
		list.add(new User(5, "Marian", 12));
		list.add(new User(6, "Wacław", 36));
		list.add(new User(7, "Gerwazy", 5));
		list.add(new User(8, "Darzbór", 13));
		list.add(new User(9, "Zbyś", 23));
		list.add(new User(10, "Władek", 32));
		list.add(new User(11, "Jurek", 55));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.springui.example.service.PagedDataService#geElements(int, int)
	 */
	@Override
	public List<User> geElements(int page, int limit) {

		if (page < 1) {
			page = 0;
			// warning
		}

		int offset = (page - 1) * limit;
		int allCount = getAllCount();
		int lastIndex = (offset + limit) >= allCount ? allCount : (offset + limit);
		logger.debug("offset  :" + offset + " to " + lastIndex);
		return list.subList(offset, lastIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.springui.example.service.PagedDataService#getAllCount()
	 */
	@Override
	public int getAllCount() {
		logger.debug("allCount {}", list.size());
		return list.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.springui.example.service.PagedDataService#getAllElements()
	 */
	@Override
	public List<User> getAllElements() {
		return list;
	}

	public User getById(int parseInt) {
		for (User u : list) {
			if (u.getUserId() == parseInt) {
				return u;
			}
		}
		return null;
	}

}
