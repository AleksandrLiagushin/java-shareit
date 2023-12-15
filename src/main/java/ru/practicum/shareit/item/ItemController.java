package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Requested item creation. ItemDto = {}", itemDto);
        return itemService.create(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseBody
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId, @RequestBody CommentDto commentDto) {
        return commentMapper.toDto(itemService.addComment(userId, itemId, commentDto));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("Requested item update. UserId = {}, itemId = {}, itemDto = {}", userId, itemId, itemDto);
        return itemService.update(userId, itemId, itemMapper.toEntity(itemDto));
    }

    @GetMapping("/{itemId}")
    public ItemForOwnerDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long itemId) {
        log.info("Requested item by id = {}", itemId);
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemForOwnerDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Requested all item by user id = {}", userId);
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findItems(@RequestParam(defaultValue = "Some text") String text) {
        log.info("Requested items search by keywords = {}", text);
        return itemService.findItems(text);
    }
}
