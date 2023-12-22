package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public Request createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @Valid @RequestBody RequestDto requestDto) {
        return requestService.create(requestDto, userId);
    }

    @GetMapping("/{requestId}")
    public RequestDto findRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long requestId) {
        return requestService.findRequestById(userId, requestId);
    }

    @GetMapping
    public List<RequestDto> getRequest(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(defaultValue = "0") @Min(0) int from,
                                               @RequestParam(defaultValue = "10") @Min(1) int size) {

        return requestService.getAllRequests(userId,
                PageRequest.of(from / size, size, Sort.by("created")
                        .descending()));
    }
}
