package com.sparta.mbti.service;

import com.sparta.mbti.dto.CommentResopnseDto;
import com.sparta.mbti.dto.ImageResponseDto;
import com.sparta.mbti.dto.PostRequestDto;
import com.sparta.mbti.dto.PostResponseDto;
import com.sparta.mbti.model.*;
import com.sparta.mbti.repository.CommentRepository;
import com.sparta.mbti.repository.ImageRepository;
import com.sparta.mbti.repository.LikesRepository;
import com.sparta.mbti.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;

    // 게시글 작성
    @Transactional
    public void createPost(User user, PostRequestDto postRequestDto) {
        // 요청한 정보로 게시글 객체 생성
        Post post = Post.builder()
                    .content(postRequestDto.getContent())
                    .tag(postRequestDto.getTag())
                    .user(user)
                    .build();

        // DB 저장
        postRepository.save(post);

        // 이미지 리스트
        List<Image> imageList = new ArrayList<>();
        for (int i = 0; i < postRequestDto.getImageList().size(); i++) {
            imageList.add(Image.builder()
                            .imageLink(postRequestDto.getImageList().get(i).getImageLink())
                            .post(post)
                            .build());
        }

        // DB 저장
        imageRepository.saveAll(imageList);

    }

    // 게시글 상세 조회
    @Transactional
    public PostResponseDto detailPost(Long postId) {
        // 게시글 조회
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("해당 게시글이 존재하지 않습니다.")
        );

        // 게시글 좋아요 수
        int likesCount = (int)likesRepository.findAllByPost(post).size();
        // 게시글 이미지 리스트
        List<Image> imageList = imageRepository.findAllByPost(post);
        // 반환할 이미지 리스트
        List<ImageResponseDto> images = new ArrayList<>();
        for (Image oneImage : imageList) {
            images.add(ImageResponseDto.builder()
                            .imageId(oneImage.getId())
                            .imageLink(oneImage.getImageLink())
                            .build());
        }
        // 게시글에 달린 댓글 리스트 (작성일자 오름차순)
        List<Comment> commentList = commentRepository.findAllByPostOrderByCreatedAtAsc(post);
        // 반환할 댓글 리스트
        List<CommentResopnseDto> comments = new ArrayList<>();
        for (Comment oneComment : commentList) {
            comments.add(CommentResopnseDto.builder()
                            .commentId(oneComment.getId())
                            .nickname(oneComment.getUser().getNickname())
                            .mbti(oneComment.getUser().getMbti().getMbti())
                            .comment(oneComment.getComment())
                            .createdAt(oneComment.getCreatedAt())
                            .build());
        }

        // 반환할 상세 게시글
        return PostResponseDto.builder()
                .postId(post.getId())
                .nickname(post.getUser().getNickname())
                .mbti(post.getUser().getMbti().getMbti())
                .content(post.getContent())
                .tag(post.getTag())
                .likesCount(likesCount)
                .imageList(images)
                .commentList(comments)
                .createdAt(post.getCreatedAt())
                .build();
    }

    // 게시글 수정
    @Transactional
    public void updatePost(Long postId, User user, PostRequestDto postRequestDto) {
        // 게시글 조회
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("해당 게시글이 존재하지 않습니다.")
        );

        if (!user.getId().equals(post.getUser().getId())) {
            throw new IllegalArgumentException("해당 게시글의 작성자만 수정 가능합니다.");
        }

        // 해당 게시글 객체 정보 업데이트
        post.update(postRequestDto);

        // DB 저장
        postRepository.save(post);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId, User user) {
        // 게시글 조회
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("해당 게시글이 존재하지 않습니다.")
        );

        if (!user.getId().equals(post.getUser().getId())) {
            throw new IllegalArgumentException("해당 게시글의 작성자만 삭제 가능합니다.");
        }

        // DB 삭제
        postRepository.deleteById(postId);
    }

    // 게시글 좋아요
    @Transactional
    public void likesOnOff(Long postId, User user) {
        // 게시글 조회
        Post findPost = postRepository.findById(postId).orElseThrow(
                () -> new NullPointerException("해당 게시글이 존재하지 않습니다.")
        );

        // 해당 사용자가 게시글에 좋아요 여부 조회
        Likes findLikes = likesRepository.findByUserAndPost(user, findPost).orElse(null);

        // 게시글에 좋아요 언했으면 추가, 했으면 삭제
        if (findLikes == null) {
            likesRepository.save(Likes.builder()
                    .user(user)
                    .post(findPost)
                    .build());
        } else {
            likesRepository.delete(findLikes);
        }
    }
}
