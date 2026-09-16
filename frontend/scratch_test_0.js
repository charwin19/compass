
    if (!localStorage.getItem('st_token')) {
      sessionStorage.setItem('st_redirect_after_login', window.location.href);
      alert('🔒 Access Restricted: Please log in to view your profile and past travel history.');
      window.location.href = 'login.html';
    }
  