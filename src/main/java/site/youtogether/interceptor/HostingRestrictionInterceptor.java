package site.youtogether.interceptor;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import site.youtogether.room.storage.RoomStorage;

@Component
@RequiredArgsConstructor
public class HostingRestrictionInterceptor implements HandlerInterceptor {

	private final RoomStorage roomStorage;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String address = request.getLocalAddr();

		if (isRoomCreationRequest(request) && isAlreadyHosting(address)) {
			return false;
		}

		return true;
	}

	private boolean isRoomCreationRequest(HttpServletRequest request) {
		return request.getMethod().equals(HttpMethod.POST.name());
	}

	private boolean isAlreadyHosting(String address) {
		return roomStorage.existsByAddress(address);
	}

}