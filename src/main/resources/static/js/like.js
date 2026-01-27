document.addEventListener("DOMContentLoaded", () => {
  const modalId = "login-modal-for-like";
  setupModalEvents(modalId);

  document.addEventListener("click", async (event) => {
    const button = event.target.closest(".like-btn");

    if (!button) return;

    const prototypeId = button.getAttribute("data-prototype-id");

    // 未ログイン時
    if (!prototypeId) {
      event.preventDefault();
      openModal(modalId);
      return;
    }

    // ログイン時の処理：fetchを実行
    event.preventDefault();

    const icon = button.querySelector(".like-icon");
    const countSpan = button.querySelector(".like-count");

    try {
      const response = await fetch(`/api/prototypes/${prototypeId}/like`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        throw new Error("いいねの処理に失敗しました");
      }

      const data = await response.json();

      // 表示の更新
      countSpan.innerText = data.LikeCount;

      if (data.isLiked) {
        icon.style.fontVariationSettings = "'FILL' 1";
        icon.style.color = "#e0245e";
      } else {
        icon.style.fontVariationSettings = "'FILL' 0";
        icon.style.color = "#657786";
      }
    } catch (error) {
      console.error("Error:", error);
      alert("エラーが発生しました。ログイン状態を確認してください。");
    }
  });
});
