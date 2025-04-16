// Mobile menu
const mobileMenuToggle = document.querySelector('.mobile-menu-toggle');
const mobileMenuClose = document.querySelector('.mobile-menu-close');
const mobileMenu = document.querySelector('.mobile-menu');

if (mobileMenuToggle) {
    mobileMenuToggle.addEventListener('click', function() {
        mobileMenu.classList.add('active');
        document.body.style.overflow = 'hidden';
    });
}

if (mobileMenuClose) {
    mobileMenuClose.addEventListener('click', function() {
        mobileMenu.classList.remove('active');
        document.body.style.overflow = '';
    });
}

// Back to top button
const backToTopButton = document.createElement('a');
backToTopButton.href = '#';
backToTopButton.className = 'back-to-top';
backToTopButton.innerHTML = '<i class="fas fa-arrow-up"></i>';
document.body.appendChild(backToTopButton);

window.addEventListener('scroll', function() {
    if (window.pageYOffset > 300) {
        backToTopButton.classList.add('active');
    } else {
        backToTopButton.classList.remove('active');
    }
});

backToTopButton.addEventListener('click', function(e) {
    e.preventDefault();
    window.scrollTo({ top: 0, behavior: 'smooth' });
});

// Product image gallery
function changeMainImage(imageUrl) {
    const mainImage = document.getElementById('main-product-image');
    if (mainImage) {
        mainImage.src = imageUrl;
    }

    // Update active thumbnail
    const thumbnails = document.querySelectorAll('.product-thumbnail');
    thumbnails.forEach(thumb => {
        thumb.classList.remove('active');
        if (thumb.querySelector('img').src === imageUrl) {
            thumb.classList.add('active');
        }
    });
}

// Quantity input
function decreaseQuantity() {
    const quantityInput = document.getElementById('quantity');
    if (quantityInput && parseInt(quantityInput.value) > 1) {
        quantityInput.value = parseInt(quantityInput.value) - 1;
    }
}

function increaseQuantity() {
    const quantityInput = document.getElementById('quantity');
    if (quantityInput) {
        const maxQuantity = parseInt(quantityInput.getAttribute('max') || 9999);
        if (parseInt(quantityInput.value) < maxQuantity) {
            quantityInput.value = parseInt(quantityInput.value) + 1;
        }
    }
}

// Product tabs
function openTab(tabId) {
    // Hide all tab contents
    const tabContents = document.querySelectorAll('.tab-content');
    tabContents.forEach(tab => tab.classList.remove('active'));

    // Show selected tab content
    const selectedTab = document.getElementById(tabId);
    if (selectedTab) {
        selectedTab.classList.add('active');
    }

    // Update tab item active state
    const tabItems = document.querySelectorAll('.tab-item');
    tabItems.forEach(item => item.classList.remove('active'));
    event.currentTarget.classList.add('active');
}

// Review star rating
function setRating(rating) {
    const stars = document.querySelectorAll('.rating-input i');
    if (stars) {
        stars.forEach((star, index) => {
            if (index < rating) {
                star.classList.remove('far');
                star.classList.add('fas');
            } else {
                star.classList.remove('fas');
                star.classList.add('far');
            }
        });
    }

    // Update hidden input value
    const ratingInput = document.querySelector('input[name="rating"]');
    if (ratingInput) {
        ratingInput.value = rating;
    }
}

// Add to cart function
function addToCart(productId, quantity = 1) {
    if (!productId) {
        const productIdElement = document.querySelector('input[name="productId"]');
        if (productIdElement) {
            productId = productIdElement.value;
        }
    }

    if (!quantity) {
        const quantityInput = document.getElementById('quantity');
        if (quantityInput) {
            quantity = quantityInput.value;
        }
    }

    if (!productId) {
        console.error('Product ID is required');
        return;
    }

    // AJAX request to add to cart
    fetch('/api/cart/items', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
        },
        body: JSON.stringify({
            productId: productId,
            quantity: quantity
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            showNotification('Sản phẩm đã được thêm vào giỏ hàng!', 'success');

            // Update cart count in header
            updateCartCount();
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification('Có lỗi xảy ra khi thêm sản phẩm vào giỏ hàng.', 'error');
        });
}

// Add to wishlist function
function addToWishlist(productId) {
    if (!productId) {
        const productIdElement = document.querySelector('input[name="productId"]');
        if (productIdElement) {
            productId = productIdElement.value;
        }
    }

    if (!productId) {
        console.error('Product ID is required');
        return;
    }

    // AJAX request to add to wishlist
    fetch('/api/wishlists', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
        },
        body: JSON.stringify({
            productId: productId
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            showNotification('Sản phẩm đã được thêm vào danh sách yêu thích!', 'success');
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification('Có lỗi xảy ra khi thêm sản phẩm vào danh sách yêu thích.', 'error');
        });
}

// Update cart count
function updateCartCount() {
    fetch('/api/cart/count')
        .then(response => response.json())
        .then(data => {
            const cartCountElement = document.querySelector('.cart-count');
            if (cartCountElement) {
                cartCountElement.textContent = data;
            }
        })
        .catch(error => console.error('Error updating cart count:', error));
}

// Show notification
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <i class="fas ${type === 'success' ? 'fa-check-circle' : type === 'error' ? 'fa-times-circle' : 'fa-info-circle'}"></i>
            <span>${message}</span>
        </div>
        <button class="notification-close"><i class="fas fa-times"></i></button>
    `;

    document.body.appendChild(notification);

    // Show notification
    setTimeout(() => {
        notification.classList.add('show');
    }, 10);

    // Auto hide after 3 seconds
    setTimeout(() => {
        hideNotification(notification);
    }, 3000);

    // Close button
    const closeButton = notification.querySelector('.notification-close');
    if (closeButton) {
        closeButton.addEventListener('click', () => {
            hideNotification(notification);
        });
    }
}

function hideNotification(notification) {
    notification.classList.remove('show');
    setTimeout(() => {
        notification.remove();
    }, 300);
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Initialize cart count
    updateCartCount();

    // Initialize any other functions here
});