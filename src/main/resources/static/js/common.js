function openModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) {
    modal.style.display = "flex";
    document.body.style.overflow = "hidden"; // 背景スクロールを止める
  }
}

/* モーダルを閉じる */
function closeModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) {
    modal.style.display = "none";
    document.body.style.overflow = "auto"; // スクロールを再開
  }
}

/* モーダルの背景クリックと閉じるボタンのイベントを設定する共通初期化 */
function setupModalEvents(modalId) {
  const modal = document.getElementById(modalId);
  if (!modal) return;

  // 背景をクリックした時
  modal.addEventListener("click", (event) => {
    // クリックされたのが背景そのもの(currentTarget)であれば閉じる
    if (event.target === event.currentTarget) {
      closeModal(modalId);
    }
  });

  // モーダル内の「×」ボタンクリックの時
  // 今後閉じるボタンなど別のボタンがあってもクラス名にmodal-close-btnを付与すれば閉じる処理ができるようにforEachを使用
  const closeBtns = modal.querySelectorAll(".modal-close-btn");
  closeBtns.forEach((btn) => {
    btn.addEventListener("click", () => closeModal(modalId));
  });
}
