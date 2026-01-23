function executeDelete(targetId) {
  closeModal("confirmModal");

  fetch(`/users/${targetId}/delete`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => {
      if (response.ok) {
        document.getElementById("successModal").style.display = "flex";
        document.body.style.overflow = "hidden";
      } else {
        alert("削除に失敗しました。");
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("削除に失敗しました。");
    });
}

function goToIndex() {
  window.location.href = "/";
}

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