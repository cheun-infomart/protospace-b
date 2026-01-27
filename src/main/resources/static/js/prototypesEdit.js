// ページ読み込み時の処理
window.addEventListener('DOMContentLoaded', () => {
  const editModal = document.getElementById("confirmModal");

  setupModalEvents("confirmModal");

  if (editModal && editModal.style.display === "flex") {
    document.body.style.overflow = "hidden";
  }

  // モーダル開く
  const openBtn = document.querySelector(".prototype-show-btn-edit");
  if (openBtn) {
    openBtn.addEventListener("click", () => openModal("confirmModal"));
  };

  //画像ファイル名の表示
  document.getElementById('image').addEventListener('change', function(e) {
    var fileName = e.target.files[0] ? e.target.files[0].name : '選択されていません';
    document.getElementById('file-name').textContent = fileName;
  });
});
