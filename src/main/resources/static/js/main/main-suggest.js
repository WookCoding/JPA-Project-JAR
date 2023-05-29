// 페이지 로드 시 호출할 함수
    const $secondContentSlide = $(".second-content-slide");

    // suggestService 객체
    const suggestService = (function () {
        function list(callback) {
            $.ajax({
                url: '/main/list-content',
                type: 'get',
                success: function (list) {
                    if (callback) {
                        callback(list);
                    }
                }
            });
        }

        return {
            list: list,
        }
    })();

    // 리스트 가져오는 함수
    // function getList(boardType) {
    //     suggestService.list(function (list) {
    //         $secondContentSlide.html(listText(list));
    //         displayPagination(list.totalPages);
    //         heartCheck(list);
    //     });
    // }

    // 리스트 내용을 HTML 텍스트로 반환하는 함수
    function listText(list) {
        let text = '';
        let suggestDTOS = list;

        suggestDTOS.forEach((suggestDTO) => {
            text += `
            <div class="content-image-box content-image-box2">
                
                    <!-- 이미지 -->
                    <div class="content-image">
                        <span class="content-image-span2">
                        <a href="/board/suggest/detail/${suggestDTO.id}">
                            <picture class="picture-position">
                            `
                            if (suggestDTO.fileDTOS.length == 0) {
            text += `<img src="https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/480px-No_image_available.svg.png" class="no-image">`;
        } else {
            for (let j = 0; j < suggestDTO.fileDTOS.length; j++) {
                if (suggestDTO.fileDTOS[j].fileType === "REPRESENTATIVE") {
                    text += `<img src="/file/display?fileName=${suggestDTO.fileDTOS[j].filePath}/${suggestDTO.fileDTOS[j].fileUuid}_${suggestDTO.fileDTOS[j].fileOriginalName}">`;
                }
            }
        }
        text += `   
                            </picture>
                            </a>
                            <div class="heart-box">
                                <button class="heart-layout" id="heart${suggestDTO.id}">
                                    <span class="auto-flex no-heart">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                                            <path fill-rule="evenodd" d="M20.5 9c0-2-1.5-3.9-3.7-3.9-2.3 0-3.8 1.63-4.8 3.33-1-1.7-2.5-3.33-4.8-3.33C5 5.1 3.5 6.867 3.5 9c0 4.62 4.949 7.667 8.5 9.623 3.551-1.956 8.5-5.003 8.5-9.623zm-19-.176C1.5 5.607 3.962 3 7 3c2.7 0 4 1 5 2.2C13 4 14.3 3 17 3c3.038 0 5.5 2.607 5.5 5.824C22.5 14.827 16.684 18.52 12 21 7.316 18.52 1.5 14.827 1.5 8.824z" fill="#FFF"></path>
                                        </svg>
                                    </span>
                                    <span class="auto-flex heart-active heart-up" style="display: none;">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                                            <path fill-rule="evenodd" d="M1.5 8.824C1.5 5.607 3.962 3 7 3c2.7 0 4 1.5 5 3 1-1.5 2.5-3 5-3 3.038 0 5.5 2.607 5.5 5.824C22.5 14.827 16.684 18.52 12 21 7.316 18.52 1.5 14.827 1.5 8.824z" fill="#fd3049"></path>
                                        </svg>
                                    </span>
                                </button>
                            </div>
                        </span>
                    </div>
                    <!-- 제목 -->
                    <div class="content-title">
                        ${suggestDTO.boardTitle}
                    </div>
                    <div class="content2">
                        ${suggestDTO.boardContent}
                    </div>
                    <!-- 좋아요, 댓글 -->
                    <div class="liked-box">
                        <div class="flex">
                            <div class="liked-layout">
                                <div class="icon-box">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="none" viewBox="0 0 24 24">
                                        <path fill-rule="evenodd" d="M1.5 8.824C1.5 5.607 3.962 3 7 3c2.5 0 4 1.5 5 3 1-1.5 2.5-3 5-3 3.038 0 5.5 2.607 5.5 5.824C22.5 14.827 16.684 18.52 12 21 7.316 18.52 1.5 14.827 1.5 8.824z" fill="#d7d7d7"></path>
                                    </svg>
                                </div>
                                <p class="like-count">${suggestDTO.likeCount}</p>
                            </div>
                            <div class="liked-layout">
                                <div class="icon-box">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="none" viewBox="0 0 24 24">
                                        <path fill-rule="evenodd" d="M2 10.68c0-.691.503-1.251 1.135-1.251h2.273V22H3.135C2.508 22 2 21.427 2 20.749V10.68zm11.361-2.508h6.366c1.255 0 2.272 1.126 2.272 2.514 0 .151-.012.301-.037.45l-1.616 9.832C20.25 21.566 19.779 22 19.23 22H7.681V9.429L11.089 0c1.704 0 2.84 1.257 2.84 3.143 0 1.257-.189 2.933-.568 5.029z" fill="#d7d7d7"></path>
                                    </svg>
                                </div>
                                <p class="reply-count">${suggestDTO.replyCount}</p>
                            </div>
                        </div>
                    </div>
            </div>
        </div>
            `;
        });
        return text;
    }
getSuggestList();


    // 좋아요 상태를 확인하는 함수
    function heartCheck(list) {
        let suggestDTOS = list.content;

        suggestDTOS.forEach((suggestDTO, i) => {
            let likeDTO = new Object();
            likeDTO.memberId = memberId;
            likeDTO.boardId = suggestDTO.id;

            likeService.heartCheck(likeDTO, function(result) {
                if (result) {
                    $($(".heart-up")[i]).hide();
                    $($(".no-heart")[i]).show();
                    $($(".no-heart")[i]).removeClass("heart-active");
                } else {
                    $($(".no-heart")[i]).addClass("heart-active");
                    $($(".heart-up")[i]).show();
                    $($(".no-heart")[i]).hide();
                }
            });
        });
    }

function getSuggestList(){
    suggestService.list(function(list){
        globalThis.suggestLength = list.length;
        if(globalThis.suggestLength  < 5){
            $secondNext.hide();
        }

        $secondContentSlide.append(listText(list));
        heartCheck(list);
    })
}

function heartCheck(list) {

    let suggestDTOS = list;
    suggestDTOS.forEach((suggestDTO, i) => {
        let likeDTO = new Object();
        likeDTO.memberId = memberId;
        likeDTO.boardId = suggestDTO.id;

        likeService.heartCheck(likeDTO,function(result){
            if(result){
                $($(".heart-up")[i]).hide();
                $($(".no-heart")[i]).show();
                $($(".no-heart")[i]).removeClass("heart-active");
            }else {
                $($(".no-heart")[i]).addClass("heart-active");
                $($(".heart-up")[i]).show();
                $($(".no-heart")[i]).hide();
            }
        });
    })
}
