// ページ読み込み時の処理
window.addEventListener('DOMContentLoaded', () => {
  const editModal = document.getElementById("confirmModal"); // HTML側のIDに合わせてください

  if (editModal && editModal.style.display === "flex") {
    document.body.style.overflow = "hidden"; // 背景スクロール禁止
  }
});

// モーダル開く
function openEditModal() {
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
function goToIndex() {
  window.location.href = "/";
}

// 外側クリック時モーダル閉じる
function closeOnOverlayClick(event, modalId) {
  const modal = document.getElementById(modalId);
  if (event.target === modal) {
    closeModal(modalId);
  }
}

//画像ファイル名の表示
document.getElementById('image').addEventListener('change', function(e) {
  var fileName = e.target.files[0] ? e.target.files[0].name : '選択されていません';
  document.getElementById('file-name').textContent = fileName;
});