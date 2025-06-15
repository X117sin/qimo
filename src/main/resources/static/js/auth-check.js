/**
 * 检查用户登录状态并根据角色显示不同的导航链接
 */
document.addEventListener('DOMContentLoaded', function() {
    // 获取存储的用户信息和令牌
    const token = localStorage.getItem('token');
    const userJson = localStorage.getItem('user');
    
    // 检查是否已登录
    if (token && userJson) {
        try {
            const user = JSON.parse(userJson);
            
            // 显示用户名
            const welcomeElement = document.getElementById('welcomeMessage');
            if (welcomeElement) {
                welcomeElement.textContent = `欢迎，${user.username}`;
            }
            
            // 根据用户角色显示不同的导航链接
            if (user.role === 'ADMIN') {
                // 管理员显示管理后台链接
                const adminLinks = document.querySelectorAll('.admin-only');
                adminLinks.forEach(link => {
                    link.style.display = 'inline-block';
                });
            }
            
            // 显示登录后的链接
            const loggedInLinks = document.querySelectorAll('.logged-in-only');
            loggedInLinks.forEach(link => {
                link.style.display = 'inline-block';
            });
            
            // 隐藏登录/注册链接
            const loggedOutLinks = document.querySelectorAll('.logged-out-only');
            loggedOutLinks.forEach(link => {
                link.style.display = 'none';
            });
        } catch (e) {
            console.error('解析用户信息失败', e);
            // 清除无效的存储数据
            localStorage.removeItem('token');
            localStorage.removeItem('user');
        }
    } else {
        // 未登录状态
        // 显示登录/注册链接
        const loggedOutLinks = document.querySelectorAll('.logged-out-only');
        loggedOutLinks.forEach(link => {
            link.style.display = 'inline-block';
        });
        
        // 隐藏需要登录的链接
        const loggedInLinks = document.querySelectorAll('.logged-in-only');
        loggedInLinks.forEach(link => {
            link.style.display = 'none';
        });
        
        // 隐藏管理员链接
        const adminLinks = document.querySelectorAll('.admin-only');
        adminLinks.forEach(link => {
            link.style.display = 'none';
        });
    }
    
    // 添加退出登录事件处理
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            // 清除存储的用户信息和令牌
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            // 跳转到登录页面
            window.location.href = '/login.html';
        });
    }
});