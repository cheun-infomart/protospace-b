// モーダル開く
function openWithdrawModal() {
  const modal = document.getElementById("confirmModal");
  if (modal) {
    modal.style.display = "flex";
    document.body.style.overflow = "hidden";
  }
}

// モーダル閉じる
function closeModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) {
    modal.style.display = "none";
    document.body.style.overflow = "auto";
  }
}

// 削除完了時モーダルを閉じて、urlの?withdrawを削除
function handleSuccessClose() {
  const modal = document.getElementById("successModal");
  if (modal) {
    modal.style.display = "none";
    document.body.style.overflow = "auto";
  }

  const url = new URL(window.location.origin);
  window.location.href = url.href;
}

// 外側クリック時モーダル閉じる
function closeOnOverlayClick(event, modalId) {
  const modal = document.getElementById(modalId);
  if (event.target === modal) {
    closeModal(modalId);
  }
}
