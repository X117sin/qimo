/**
 * 认证工具函数库
 * 提供JWT令牌处理、错误处理等通用功能
 */

/**
 * 处理API请求的响应，特别处理403错误（通常是token过期）
 * @param {Response} response - fetch API的响应对象
 * @param {string} errorMessage - 默认错误消息
 * @param {boolean} silentAuth - 是否静默处理认证错误，不跳转登录页面
 * @returns {Promise} - 处理后的Promise
 */
function handleApiResponse(response, errorMessage = '请求失败', silentAuth = false) {

    if (!response.ok) {
        // 检查是否是401或403错误（认证问题，通常是token过期或无效）
        if (response.status === 401 || response.status === 403) {
            if (silentAuth) {
                // 静默模式下，只返回错误，不清除认证数据和跳转
                throw new Error('认证失败');
            } else {
                // 清除本地存储的token和用户信息
                clearAuthData();
                throw new Error('登录已过期，请重新登录');
            }
        }
        throw new Error(errorMessage);
    }
    return response.json();
}

/**
 * 处理API请求错误，特别处理登录过期的情况
 * @param {Error} error - 捕获的错误
 * @param {string} logPrefix - 日志前缀
 * @param {boolean} silentAuth - 是否静默处理认证错误，不跳转登录页面
 * @param {Function} onAuthError - 认证错误时的回调函数
 */
function handleApiError(error, logPrefix = '请求失败', silentAuth = false, onAuthError = null) {
    console.error(`${logPrefix}:`, error);
    
    // 如果是认证失败且提供了回调函数
    if (silentAuth && (error.message.includes('认证失败') || error.message.includes('登录已过期')) && onAuthError) {
        onAuthError(error);
        return;
    }
    
    // 显示错误消息
    alert(`${logPrefix}: ${error.message}`);
    
    // 如果是登录过期或认证失败，跳转到登录页面
    if (error.message.includes('登录已过期') || error.message.includes('认证失败')) {
        window.location.href = '/login.html';
    }
}

/**
 * 清除认证数据（token和用户信息）
 */
function clearAuthData() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    
    // 同时清除Cookie中的token
    document.cookie = 'token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT';
}

/**
 * 获取认证头信息
 * @returns {Object} 包含Authorization头的对象
 */
function getAuthHeaders() {
    const token = localStorage.getItem('token');
    if (!token) {
        return {
            'Content-Type': 'application/json'
        };
    }
    return {
        'Authorization': 'Bearer ' + token,
        'Content-Type': 'application/json'
    };
}

/**
 * 设置认证token到Cookie和localStorage
 * @param {string} token - JWT token
 */
function setAuthToken(token) {
    // 存储到localStorage
    localStorage.setItem('token', token);
    
    // 同时存储到Cookie，供后端读取
    document.cookie = `token=${token}; path=/; max-age=86400; SameSite=Strict`;
}

/**
 * 检查用户是否已登录
 * @returns {boolean} 是否已登录
 */
function isLoggedIn() {
    return !!localStorage.getItem('token');
}

/**
 * 重定向到登录页面
 */
function redirectToLogin() {
    window.location.href = '/login.html';
}

// 在页面加载时检查token状态（但不在管理员页面执行）
window.addEventListener('load', function() {
    // 如果是管理员页面，跳过自动检查，让管理员页面自己处理认证
    if (window.location.pathname.includes('admin-dashboard.html')) {
        return;
    }
    
    if (isLoggedIn()) {
        const token = localStorage.getItem('token');
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            const currentTime = Date.now() / 1000;
            const timeUntilExpiry = payload.exp - currentTime;
            // 如果token在2小时内过期，显示提醒
            if (timeUntilExpiry < 7200 && timeUntilExpiry > 0) {
                const hours = Math.floor(timeUntilExpiry / 3600);
                const minutes = Math.floor((timeUntilExpiry % 3600) / 60);
                const timeText = hours > 0 ? `${hours}小时${minutes}分钟` : `${minutes}分钟`;
                if (confirm(`您的登录状态将在${timeText}后过期，是否现在重新登录？`)) {
                    // 执行刷新逻辑或重新登录
                    window.location.href = '/login.html';
                }
            }
        } catch (error) {
            console.error('检查token状态失败:', error);
        }
    }
}); // 结束页面加载事件监听器
