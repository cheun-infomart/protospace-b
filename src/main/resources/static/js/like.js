document.addEventListener("DOMContentLoaded", () => {
  const modal = document.getElementById("login-modal-for-like");
  const closeBtn = document.getElementById("close-login-modal");

  document.addEventListener("click", async (event) => {
    const button = event.target.closest(".like-btn");

    if (!button) return;

    const prototypeId = button.getAttribute("data-prototype-id");

    // 未ログイン時
    if (!prototypeId) {
      event.preventDefault();

      // クリックされた瞬間に改めてモーダルをHTMLから探す
      const modal = document.getElementById("login-modal-for-like");

      if (modal) {
        modal.style.display = "flex";

        // モーダル内の「✖」ボタンで閉じる処理
        const closeBtn = modal.querySelector("#modal-close-btn");
        closeBtn.onclick = () => {
          modal.style.display = "none";
        };

        // 背景クリックで閉じる処理
        modal.onclick = (event) => {
          if (event.target === modal) modal.style.display = "none";
        };
      } else {
        console.error(
          "モーダルが見つかりません。HTMLのth:replaceを確認してください。",
        );
      }
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
