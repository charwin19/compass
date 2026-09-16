/**
 * main.js — Global utilities shared across all pages
 * Compass
 */

/* ============================================================
   AUTH STATE HELPERS
   ============================================================ */
const Auth = {
  isLoggedIn() {
    return !!localStorage.getItem('st_token');
  },
  getUser() {
    try {
      return JSON.parse(localStorage.getItem('st_user') || 'null');
    } catch {
      return null;
    }
  },
  requireAuth(requiredRole = null) {
    if (!this.isLoggedIn()) {
      sessionStorage.setItem('st_redirect_after_login', window.location.href);
      alert('🔒 Access Restricted: Please sign in to access this feature.');
      window.location.href = 'login.html';
      return false;
    }
    if (requiredRole) {
      const user = this.getUser();
      if (user?.role !== requiredRole) {
        alert('⛔ Access Denied: Admin privileges required for this portal.');
        window.location.href = 'index.html';
        return false;
      }
    }
    return true;
  },
  logout() {
    localStorage.removeItem('st_token');
    localStorage.removeItem('st_user');
    window.location.href = 'login.html';
  }
};

/* ============================================================
   USER PROFILE & PAST TRAVEL STORE
   ============================================================ */
const UserProfileStore = {
  getProfile() {
    let u = Auth.getUser();
    if (!u) return null;

    // Fill default profile fields if not already populated
    const defaults = {
      avatar: '🧭',
      phone: '+91 98765 43210',
      homeCity: 'Bangalore, Karnataka',
      travelStyle: 'Moderate Explorer',
      emergencyContact: '+91 98450 11223 (Spouse/Parent)',
      bio: 'Passionate globetrotter discovering India’s scenic mountains, hidden heritage, and vibrant local cuisines.',
      memberSince: 'March 2024'
    };

    return Object.assign({}, defaults, u);
  },

  updateProfile(updatedData) {
    const current = this.getProfile() || {};
    const merged = Object.assign({}, current, updatedData);
    localStorage.setItem('st_user', JSON.stringify(merged));
    if (typeof updateNavAuth === 'function') updateNavAuth();
    return merged;
  },

  _getUserTripsKey() {
    const u = Auth.getUser();
    if (!u || !u.email) return 'st_user_trips';
    const safeEmail = u.email.toLowerCase().trim().replace(/[^a-z0-9]/g, '_');
    return `st_trips_${safeEmail}`;
  },

  getPastTrips() {
    const u = Auth.getUser();
    const key = this._getUserTripsKey();
    const raw = localStorage.getItem(key);

    if (raw !== null) {
      try { return JSON.parse(raw); } catch { return []; }
    }

    // Only the default Demo Traveler account starts with seeded sample trips.
    // Real registered users or other accounts start with 0 trips ([]).
    if (u && u.email && u.email.toLowerCase() === 'traveler@smarttourism.com') {
      const defaultPastTrips = [
        {
          id: 'trip-101',
          destination: 'Munnar',
          title: 'Misty Munnar Tea Trails & High Peaks Tour',
          dates: '12 Aug 2024 – 15 Aug 2024',
          duration: '3 Days',
          travelers: '2 Persons (Couple)',
          budgetSpent: '₹16,800',
          rating: '★★★★★',
          status: 'COMPLETED',
          coverImg: 'https://images.unsplash.com/photo-1591779051696-1c3fa1469a79?w=600&q=80',
          highlights: 'Eravikulam Nilgiri Tahr safari, Mattupetty Speedboating, Sunrise hike at Top Station',
          reviewStory: 'Munnar was simply breathtaking. Walking through endless rolling green tea terraces in the morning mist felt like pure bliss. The local tea museum was a delightful experience!',
          proTip: 'Leave for Top Station before 6:30 AM to catch the sea of clouds above the valley.'
        },
        {
          id: 'trip-102',
          destination: 'Mysore',
          title: 'Royal Mysore Palace & Heritage Exploration',
          dates: '18 Apr 2024 – 20 Apr 2024',
          duration: '2 Days',
          travelers: '3 Persons (Family)',
          budgetSpent: '₹11,500',
          rating: '★★★★★',
          status: 'COMPLETED',
          coverImg: 'https://images.unsplash.com/photo-1600100397608-f010f443b811?w=600&q=80',
          highlights: 'Mysore Palace night illumination, Chamundi Hill Temple, Devaraja Spice Market',
          reviewStory: 'Seeing the 100,000 bulbs illuminating Mysore Palace on Sunday evening left our family in awe. The local filter coffee and Mysore Pak were out of this world.',
          proTip: 'Book palace tickets online to skip the weekend entry queue.'
        },
        {
          id: 'trip-103',
          destination: 'Kodaikanal',
          title: 'Princess of Hills Lakeside Retreat & Trekking',
          dates: '05 Jan 2024 – 08 Jan 2024',
          duration: '4 Days',
          travelers: '2 Persons (Couple)',
          budgetSpent: '₹22,000',
          rating: '★★★★☆',
          status: 'COMPLETED',
          coverImg: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=600&q=80',
          highlights: 'Star-shaped lake boating, Coaker’s Walk mist view, Bryant Park, Pine forest meditation',
          reviewStory: 'The crisp, clean mountain air and quiet evenings by the lake were deeply rejuvenating. Coaker’s walk during sunset was picture perfect.',
          proTip: 'Rent a tandem bicycle for a full round around the lake at 7:30 AM before tourist crowds.'
        }
      ];

      localStorage.setItem(key, JSON.stringify(defaultPastTrips));
      return defaultPastTrips;
    }

    // Default for any user who hasn't logged trips is 0 trips
    localStorage.setItem(key, JSON.stringify([]));
    return [];
  },

  addTrip(trip) {
    const trips = this.getPastTrips();
    trip.id = 'trip-' + Date.now();
    trip.createdAt = new Date().toISOString();
    trips.unshift(trip);
    const key = this._getUserTripsKey();
    localStorage.setItem(key, JSON.stringify(trips));

    const u = Auth.getUser();
    if (u && u.email && typeof AdminUserStore !== 'undefined') {
      AdminUserStore.updateUser(u.email, { 
        lastTravel: `${trip.destination} (${trip.dates || 'Recent'})` 
      });
    }
    return trips;
  },

  deleteTrip(tripId) {
    const trips = this.getPastTrips().filter(t => t.id !== tripId);
    const key = this._getUserTripsKey();
    localStorage.setItem(key, JSON.stringify(trips));

    const u = Auth.getUser();
    if (u && u.email && typeof AdminUserStore !== 'undefined') {
      AdminUserStore.updateUser(u.email, { 
        lastTravel: trips.length > 0 ? `${trips[0].destination} (${trips[0].dates || 'Recent'})` : null 
      });
    }
    return trips;
  },

  getLastTrip() {
    const trips = this.getPastTrips();
    return (trips && trips.length > 0) ? trips[0] : null;
  },

  clearAllTrips() {
    const key = this._getUserTripsKey();
    localStorage.setItem(key, JSON.stringify([]));

    const u = Auth.getUser();
    if (u && u.email && typeof AdminUserStore !== 'undefined') {
      AdminUserStore.updateUser(u.email, { lastTravel: null });
    }
    return [];
  },

  resetDefaultTrips() {
    const key = this._getUserTripsKey();
    const sampleTrips = [
      {
        id: 'trip-101',
        destination: 'Munnar',
        title: 'Misty Munnar Tea Trails & High Peaks Tour',
        dates: '12 Aug 2024 – 15 Aug 2024',
        duration: '3 Days',
        travelers: '2 Persons (Couple)',
        budgetSpent: '₹16,800',
        rating: '★★★★★',
        status: 'COMPLETED',
        coverImg: 'https://images.unsplash.com/photo-1591779051696-1c3fa1469a79?w=600&q=80',
        highlights: 'Eravikulam Nilgiri Tahr safari, Mattupetty Speedboating, Sunrise hike at Top Station',
        reviewStory: 'Munnar was simply breathtaking. Walking through endless rolling green tea terraces in the morning mist felt like pure bliss. The local tea museum was a delightful experience!',
        proTip: 'Leave for Top Station before 6:30 AM to catch the sea of clouds above the valley.'
      },
      {
        id: 'trip-102',
        destination: 'Mysore',
        title: 'Royal Mysore Palace & Heritage Exploration',
        dates: '18 Apr 2024 – 20 Apr 2024',
        duration: '2 Days',
        travelers: '3 Persons (Family)',
        budgetSpent: '₹11,500',
        rating: '★★★★★',
        status: 'COMPLETED',
        coverImg: 'https://images.unsplash.com/photo-1600100397608-f010f443b811?w=600&q=80',
        highlights: 'Mysore Palace night illumination, Chamundi Hill Temple, Devaraja Spice Market',
        reviewStory: 'Seeing the 100,000 bulbs illuminating Mysore Palace on Sunday evening left our family in awe. The local filter coffee and Mysore Pak were out of this world.',
        proTip: 'Book palace tickets online to skip the weekend entry queue.'
      },
      {
        id: 'trip-103',
        destination: 'Kodaikanal',
        title: 'Princess of Hills Lakeside Retreat & Trekking',
        dates: '05 Jan 2024 – 08 Jan 2024',
        duration: '4 Days',
        travelers: '2 Persons (Couple)',
        budgetSpent: '₹22,000',
        rating: '★★★★☆',
        status: 'COMPLETED',
        coverImg: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=600&q=80',
        highlights: 'Star-shaped lake boating, Coaker’s Walk mist view, Bryant Park, Pine forest meditation',
        reviewStory: 'The crisp, clean mountain air and quiet evenings by the lake were deeply rejuvenating. Coaker’s walk during sunset was picture perfect.',
        proTip: 'Rent a tandem bicycle for a full round around the lake at 7:30 AM before tourist crowds.'
      }
    ];

    localStorage.setItem(key, JSON.stringify(sampleTrips));
    const u = Auth.getUser();
    if (u && u.email && typeof AdminUserStore !== 'undefined') {
      AdminUserStore.updateUser(u.email, { lastTravel: 'Munnar (Aug 2024)' });
    }
    return sampleTrips;
  }
};

/* ============================================================
   ADMIN USER STORE — Monitor, manage and edit users
   ============================================================ */
const AdminUserStore = {
  KEY: 'st_registered_users',

  _getRegisteredMap() {
    try {
      const stored = localStorage.getItem(this.KEY);
      if (stored) return JSON.parse(stored);
    } catch {
      // Fallback
    }

    // Default Seed Accounts
    const defaultUsers = {
      'traveler@smarttourism.com': {
        fullName: 'Demo Traveler',
        email: 'traveler@smarttourism.com',
        phone: '+91 98765 43210',
        homeCity: 'Bangalore, Karnataka',
        travelStyle: 'Moderate Explorer',
        emergencyContact: '+91 98450 11223 (Spouse)',
        bio: 'Passionate globetrotter discovering scenic hill stations, heritage gems, and local cuisines.',
        role: 'ROLE_USER',
        status: 'ACTIVE',
        avatar: '🧭',
        lastTravel: 'Munnar (Aug 2024)',
        joinedDate: '15 Jan 2024'
      },
      'priya.ramesh@gmail.com': {
        fullName: 'Priya Ramesh',
        email: 'priya.ramesh@gmail.com',
        phone: '+91 94432 10990',
        homeCity: 'Chennai, Tamil Nadu',
        travelStyle: 'Family Vacations',
        emergencyContact: '+91 94430 55441 (Brother)',
        bio: 'Avid traveler and tea lover. Frequently visiting Nilgiris and Western Ghats.',
        role: 'ROLE_USER',
        status: 'ACTIVE',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Priya&backgroundColor=b6e3f4',
        lastTravel: 'Ooty (May 2024)',
        joinedDate: '02 Feb 2024'
      },
      'karthik.nair@outlook.com': {
        fullName: 'Karthik Nair',
        email: 'karthik.nair@outlook.com',
        phone: '+91 98450 67890',
        homeCity: 'Bengaluru, Karnataka',
        travelStyle: 'Adventure & Trekking',
        emergencyContact: '+91 98450 12345 (Father)',
        bio: 'Hiker, wildlife enthusiast, and mountain photographer.',
        role: 'ROLE_USER',
        status: 'ACTIVE',
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Karthik&backgroundColor=c0aede',
        lastTravel: 'Kodaikanal (Jan 2024)',
        joinedDate: '10 Mar 2024'
      },
      'vikram.mehta@traveler.in': {
        fullName: 'Vikram Mehta',
        email: 'vikram.mehta@traveler.in',
        phone: '+91 91234 56780',
        homeCity: 'Mumbai, Maharashtra',
        travelStyle: 'Budget / Backpacker',
        emergencyContact: '+91 91234 99999 (Mother)',
        bio: 'New traveler looking forward to my first journey to South India.',
        role: 'ROLE_USER',
        status: 'ACTIVE',
        avatar: '🎒',
        lastTravel: null, // Explicit null test case!
        joinedDate: '08 Sep 2024'
      },
      'admin@smarttourism.com': {
        fullName: 'Platform Admin',
        email: 'admin@smarttourism.com',
        phone: '+91 98765 00000',
        homeCity: 'New Delhi, India',
        travelStyle: 'Luxury / Premium',
        emergencyContact: '+91 98765 99999 (Control Desk)',
        bio: 'Administrator overseeing safety dispatch, guide verification, and traveler support.',
        role: 'ROLE_ADMIN',
        status: 'ACTIVE',
        avatar: '🛡️',
        lastTravel: 'Mysore (Apr 2024)',
        joinedDate: '01 Jan 2024'
      }
    };

    localStorage.setItem(this.KEY, JSON.stringify(defaultUsers));
    return defaultUsers;
  },

  getAllUsers() {
    const map = this._getRegisteredMap();
    return Object.values(map);
  },

  getUserByEmail(email) {
    if (!email) return null;
    const map = this._getRegisteredMap();
    return map[email.toLowerCase().trim()] || null;
  },

  updateUser(email, updatedData) {
    const map = this._getRegisteredMap();
    const key = email.toLowerCase().trim();
    if (!map[key]) return null;

    map[key] = Object.assign({}, map[key], updatedData);
    localStorage.setItem(this.KEY, JSON.stringify(map));

    // If currently logged in user was modified, sync st_user
    const currentUser = Auth.getUser();
    if (currentUser && currentUser.email && currentUser.email.toLowerCase() === key) {
      const merged = Object.assign({}, currentUser, updatedData);
      localStorage.setItem('st_user', JSON.stringify(merged));
      if (typeof updateNavAuth === 'function') updateNavAuth();
    }

    return map[key];
  },

  addUser(userData) {
    const map = this._getRegisteredMap();
    const email = (userData.email || '').toLowerCase().trim();
    if (!email) return false;

    map[email] = Object.assign({
      fullName: 'New Traveler',
      email,
      phone: '',
      homeCity: 'India',
      travelStyle: 'Moderate Explorer',
      role: 'ROLE_USER',
      status: 'ACTIVE',
      avatar: '🧭',
      lastTravel: null,
      joinedDate: new Date().toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })
    }, userData);

    localStorage.setItem(this.KEY, JSON.stringify(map));
    return map[email];
  },

  deleteUser(email) {
    const map = this._getRegisteredMap();
    const key = email.toLowerCase().trim();
    if (map[key]) {
      delete map[key];
      localStorage.setItem(this.KEY, JSON.stringify(map));
      return true;
    }
    return false;
  }
};

/* ============================================================
   GUIDES STORE — Child 2: Local Guides & Verification
   ============================================================ */
const GuidesStore = {
  KEY: 'st_guides_data',

  _getDefaultGuides() {
    return [
      {
        id: 'guide-1',
        name: 'Murugan Selvan',
        destination: 'Ooty',
        state: 'Tamil Nadu',
        experienceYears: 8,
        rating: 4.9,
        reviewCount: 142,
        dailyFee: '₹1,200/day',
        phone: '+91 94432 10981',
        languages: 'English, Tamil, Hindi',
        specialization: 'Wildlife Trails, Nilgiri Tea Estates & Hidden Viewpoints',
        bio: 'Born and raised in the Nilgiri hills. Certified eco-tourism mountaineer guiding travelers across Ooty, Coonoor, and Pykara for over 8 years.',
        photo: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&q=80',
        isVerified: true,
        verifiedAt: '12 Jan 2024 by Admin',
        badges: ['Certified Eco-Guide', 'First-Aid Trained']
      },
      {
        id: 'guide-2',
        name: 'Anitha Ramesh',
        destination: 'Ooty',
        state: 'Tamil Nadu',
        experienceYears: 5,
        rating: 4.8,
        reviewCount: 98,
        dailyFee: '₹1,000/day',
        phone: '+91 98421 65432',
        languages: 'English, Tamil',
        specialization: 'Botanical Gardens, Colonial Heritage & Nature Photography',
        bio: 'Local botanist and cultural storyteller specializing in relaxed family tours and nature photography across Ooty.',
        photo: 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=300&q=80',
        isVerified: true,
        verifiedAt: '04 Mar 2024 by Admin',
        badges: ['Heritage Expert', 'Family Friendly']
      },
      {
        id: 'guide-3',
        name: 'Rajesh Kannan',
        destination: 'Kodaikanal',
        state: 'Tamil Nadu',
        experienceYears: 7,
        rating: 4.9,
        reviewCount: 115,
        dailyFee: '₹1,100/day',
        phone: '+91 94435 88990',
        languages: 'English, Tamil, Malayalam',
        specialization: 'Pine Forest Meditations, Dolphin Nose Treks & Cycling Trails',
        bio: 'Licensed mountain guide specialized in offbeat trails around Kodai lake and deep Shola forest hikes.',
        photo: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&q=80',
        isVerified: true,
        verifiedAt: '18 Feb 2024 by Admin',
        badges: ['Trek Leader', 'Forest Certified']
      },
      {
        id: 'guide-4',
        name: 'Meenakshi Sundaram',
        destination: 'Kodaikanal',
        state: 'Tamil Nadu',
        experienceYears: 3,
        rating: 4.6,
        reviewCount: 41,
        dailyFee: '₹900/day',
        phone: '+91 98412 33441',
        languages: 'English, Tamil',
        specialization: 'Kodai Lake Boating, Pillar Rocks & Chocolate Making Tours',
        bio: 'Enthusiastic young local guide with in-depth knowledge of homemade artisan chocolates and scenic picnic spots.',
        photo: 'https://images.unsplash.com/photo-1580489944761-15a19d654956?w=300&q=80',
        isVerified: false, // Pending verification test case!
        verifiedAt: null,
        badges: ['Culinary Guide']
      },
      {
        id: 'guide-5',
        name: 'Jayan Joseph',
        destination: 'Munnar',
        state: 'Kerala',
        experienceYears: 10,
        rating: 4.9,
        reviewCount: 189,
        dailyFee: '₹1,400/day',
        phone: '+91 94471 22334',
        languages: 'Malayalam, English, Tamil, Hindi',
        specialization: 'Top Station Sunrise Hikes, Eravikulam Nilgiri Tahr & Tea Museums',
        bio: 'Award-winning naturalist with a decade of expertise leading trekking groups through Munnar’s mist-shrouded peaks.',
        photo: 'https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=300&q=80',
        isVerified: true,
        verifiedAt: '22 Jan 2024 by Admin',
        badges: ['Master Naturalist', 'High Altitude Certified']
      },
      {
        id: 'guide-6',
        name: 'Arun Nair',
        destination: 'Munnar',
        state: 'Kerala',
        experienceYears: 4,
        rating: 4.7,
        reviewCount: 56,
        dailyFee: '₹1,000/day',
        phone: '+91 94478 99001',
        languages: 'Malayalam, English',
        specialization: 'Spice Plantations & Mattupetty Dam Kayaking',
        bio: 'Passionate wildlife photographer and kayaking instructor introducing travelers to secret water bodies in Munnar.',
        photo: 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=300&q=80',
        isVerified: false, // Pending verification test case!
        verifiedAt: null,
        badges: ['Kayaking Instructor']
      },
      {
        id: 'guide-7',
        name: 'Rangaraj Rao',
        destination: 'Mysore',
        state: 'Karnataka',
        experienceYears: 12,
        rating: 5.0,
        reviewCount: 230,
        dailyFee: '₹1,500/day',
        phone: '+91 98450 77112',
        languages: 'Kannada, English, Hindi',
        specialization: 'Mysore Palace Royal Architecture, Dasara Traditions & Devaraja Bazaar',
        bio: 'Senior cultural historian and official heritage interpreter for royal Mysore architecture and silk weaving guilds.',
        photo: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=300&q=80',
        isVerified: true,
        verifiedAt: '05 Jan 2024 by Admin',
        badges: ['Royal Palace Certified', 'Historian']
      },
      {
        id: 'guide-8',
        name: 'Deepa Murthy',
        destination: 'Mysore',
        state: 'Karnataka',
        experienceYears: 6,
        rating: 4.8,
        reviewCount: 84,
        dailyFee: '₹1,100/day',
        phone: '+91 98459 33221',
        languages: 'Kannada, English',
        specialization: 'Sandalwood Craft, Mysore Pak Cooking & Chamundi Hills',
        bio: 'Warm and friendly heritage walk leader taking visitors through centuries-old spice lanes and royal culinary kitchens.',
        photo: 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=300&q=80',
        isVerified: true,
        verifiedAt: '15 Mar 2024 by Admin',
        badges: ['Culinary Heritage']
      },
      {
        id: 'guide-9',
        name: 'Senthil Kumar',
        destination: 'Coimbatore',
        state: 'Tamil Nadu',
        experienceYears: 6,
        rating: 4.7,
        reviewCount: 62,
        dailyFee: '₹1,000/day',
        phone: '+91 98940 11223',
        languages: 'Tamil, English',
        specialization: 'Adiyogi Meditation, Siruvani Waterfalls & Nilgiri Foothills',
        bio: 'Spiritual and nature tour planner guiding travelers to serene ashrams and western ghat foothill sanctuaries.',
        photo: 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=300&q=80',
        isVerified: false, // Pending verification test case!
        verifiedAt: null,
        badges: ['Spiritual Walk Leader']
      },
      {
        id: 'guide-10',
        name: 'Dinesh Rajan',
        destination: 'Chennai',
        state: 'Tamil Nadu',
        experienceYears: 9,
        rating: 4.8,
        reviewCount: 160,
        dailyFee: '₹1,300/day',
        phone: '+91 98401 55667',
        languages: 'Tamil, English, Hindi',
        specialization: 'Mahabalipuram Shore Temples, Marina Beach Sunrise & Mylapore Culture',
        bio: 'Archaeological history enthusiast and UNESCO monument guide with extensive storytelling experience.',
        photo: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&q=80',
        isVerified: true,
        verifiedAt: '10 Feb 2024 by Admin',
        badges: ['UNESCO Heritage Guide']
      }
    ];
  },

  getAll() {
    const raw = localStorage.getItem(this.KEY);
    if (raw) {
      try { return JSON.parse(raw); } catch { return []; }
    }
    const defaults = this._getDefaultGuides();
    localStorage.setItem(this.KEY, JSON.stringify(defaults));
    return defaults;
  },

  getById(id) {
    const guides = this.getAll();
    return guides.find(g => g.id === id) || null;
  },

  getByDestination(dest) {
    const guides = this.getAll();
    if (!dest || dest === 'ALL') return guides;
    return guides.filter(g => g.destination.toLowerCase() === dest.toLowerCase());
  },

  verifyGuide(id, status = true) {
    const guides = this.getAll();
    const idx = guides.findIndex(g => g.id === id);
    if (idx !== -1) {
      guides[idx].isVerified = Boolean(status);
      guides[idx].verifiedAt = status ? `${new Date().toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })} by Admin` : null;
      localStorage.setItem(this.KEY, JSON.stringify(guides));
      return guides[idx];
    }
    return null;
  },

  updateGuide(id, updatedData) {
    const guides = this.getAll();
    const idx = guides.findIndex(g => g.id === id);
    if (idx !== -1) {
      guides[idx] = Object.assign({}, guides[idx], updatedData);
      localStorage.setItem(this.KEY, JSON.stringify(guides));
      return guides[idx];
    }
    return null;
  },

  addGuide(guideData) {
    const guides = this.getAll();
    const newGuide = Object.assign({
      id: 'guide-' + Date.now(),
      name: 'Local Guide',
      destination: 'Ooty',
      state: 'Tamil Nadu',
      experienceYears: 3,
      rating: 4.8,
      reviewCount: 1,
      dailyFee: '₹1,000/day',
      phone: '+91 98000 00000',
      languages: 'English, Tamil',
      specialization: 'Custom Mountain Treks',
      bio: 'Verified local guide dedicated to safe, authentic travel journeys.',
      photo: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=300&q=80',
      isVerified: true,
      verifiedAt: `${new Date().toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })} by Admin`,
      badges: ['Verified Local']
    }, guideData);

    guides.unshift(newGuide);
    localStorage.setItem(this.KEY, JSON.stringify(guides));
    return newGuide;
  },

  deleteGuide(id) {
    let guides = this.getAll();
    guides = guides.filter(g => g.id !== id);
    localStorage.setItem(this.KEY, JSON.stringify(guides));
    return guides;
  },

  resetDefaults() {
    const defaults = this._getDefaultGuides();
    localStorage.setItem(this.KEY, JSON.stringify(defaults));
    return defaults;
  }
};

/* Attach stores to window */
window.UserProfileStore = UserProfileStore;
window.AdminUserStore   = AdminUserStore;
window.GuidesStore      = GuidesStore;

/* ============================================================
   TOAST NOTIFICATIONS
   ============================================================ */
const Toast = {
  container: null,

  _getContainer() {
    if (!this.container) {
      this.container = document.getElementById('toast-container');
      if (!this.container) {
        this.container = document.createElement('div');
        this.container.className = 'toast-container';
        this.container.id = 'toast-container';
        document.body.appendChild(this.container);
      }
    }
    return this.container;
  },

  show(message, type = 'info', duration = 3500) {
    const container = this._getContainer();

    const icons = {
      success: '<i class="fa-solid fa-circle-check" style="color:#10b981"></i>',
      error:   '<i class="fa-solid fa-circle-xmark" style="color:#ef4444"></i>',
      info:    '<i class="fa-solid fa-circle-info" style="color:#0ea5e9"></i>',
    };

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.setAttribute('role', 'alert');
    toast.innerHTML = `
      ${icons[type] || icons.info}
      <span style="flex:1;font-size:0.875rem">${message}</span>
      <button onclick="this.parentElement.remove()" aria-label="Dismiss" style="background:none;color:var(--clr-text-muted);font-size:1rem;padding:0 4px;cursor:pointer;border:none;">✕</button>
    `;

    container.appendChild(toast);
    setTimeout(() => toast.remove(), duration);
  },

  success(msg, dur) { this.show(msg, 'success', dur); },
  error(msg, dur)   { this.show(msg, 'error', dur); },
  info(msg, dur)    { this.show(msg, 'info', dur); },
};

/* ============================================================
   UTILITY HELPERS
   ============================================================ */
const Utils = {
  /** Format number as Indian Rupee */
  formatINR(amount) {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0
    }).format(amount);
  },

  /** Format date as "10 Sep 2024" */
  formatDate(dateStr) {
    return new Date(dateStr).toLocaleDateString('en-IN', {
      day: 'numeric', month: 'short', year: 'numeric'
    });
  },

  /** Render filled star rating HTML */
  renderStars(rating) {
    const full  = Math.floor(rating);
    const half  = rating % 1 >= 0.5;
    const empty = 5 - full - (half ? 1 : 0);
    return (
      '<i class="fa-solid fa-star"></i>'.repeat(full) +
      (half ? '<i class="fa-solid fa-star-half-stroke"></i>' : '') +
      '<i class="fa-regular fa-star"></i>'.repeat(empty)
    );
  },

  /** Get URL query param */
  getParam(name) {
    return new URLSearchParams(window.location.search).get(name);
  },

  /** Debounce */
  debounce(fn, delay = 300) {
    let timer;
    return (...args) => {
      clearTimeout(timer);
      timer = setTimeout(() => fn(...args), delay);
    };
  },

  /** Get current geolocation (returns Promise) */
  getLocation() {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject(new Error('Geolocation is not supported by your browser.'));
        return;
      }
      navigator.geolocation.getCurrentPosition(
        pos => resolve({ lat: pos.coords.latitude, lng: pos.coords.longitude }),
        err => reject(new Error(err.message)),
        { timeout: 10000, enableHighAccuracy: true }
      );
    });
  },
};

/* ============================================================
   API CLIENT — centralised fetch wrapper
   ============================================================ */
const API_BASE = 'http://localhost:8080/api';

const ApiClient = {
  async request(method, path, data = null) {
    const token = localStorage.getItem('st_token');
    const headers = { 'Content-Type': 'application/json' };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const config = { method, headers };
    if (data) config.body = JSON.stringify(data);

    try {
      const res = await fetch(`${API_BASE}${path}`, config);

      if (res.status === 401) {
        Auth.logout();
        return;
      }

      const json = await res.json().catch(() => ({}));

      if (!res.ok) {
        throw new Error(json.message || `HTTP ${res.status}`);
      }

      return json;
    } catch (err) {
      if (err.name === 'TypeError' && err.message.includes('fetch')) {
        console.warn('Backend not reachable — running in offline/mock mode');
        return null; // caller handles null as offline mode
      }
      throw err;
    }
  },

  get(path)         { return this.request('GET',    path); },
  post(path, data)  { return this.request('POST',   path, data); },
  patch(path, data) { return this.request('PATCH',  path, data); },
  del(path)         { return this.request('DELETE', path); },
};

/* Attach core items to window early */
window.Auth             = Auth;
window.UserProfileStore = UserProfileStore;
window.Toast            = Toast;
window.ApiClient        = ApiClient;
window.Utils            = Utils;

/* ============================================================
   PAGE ROUTE GUARDS & OPTION ACCESS INTERCEPTOR
   ============================================================ */
(function initAuthGuards() {
  // 1. Page-level direct URL guard
  const path = window.location.pathname.toLowerCase();
  const currentPage = path.split('/').pop() || 'index.html';
  const protectedPages = ['planner.html', 'weather.html', 'emergency.html', 'admin-dashboard.html', 'profile.html'];

  if (protectedPages.includes(currentPage)) {
    const requiredRole = (currentPage === 'admin-dashboard.html') ? 'ROLE_ADMIN' : null;
    Auth.requireAuth(requiredRole);
  }

  // 2. Global Option Click Interceptor across all pages
  document.addEventListener('click', (e) => {
    const link = e.target.closest('a, button');
    if (!link) return;

    // Check href or data-protected attribute
    const href = link.getAttribute('href') || '';
    const isProtectedTarget = /planner\.html|weather\.html|emergency\.html|admin-dashboard\.html|profile\.html/i.test(href);

    if (isProtectedTarget && !Auth.isLoggedIn()) {
      e.preventDefault();
      e.stopPropagation();

      // Remember where user was trying to go
      sessionStorage.setItem('st_redirect_after_login', href);

      alert('🔒 Access Restricted: Please sign in to access this option.');
      window.location.href = 'login.html';
    }
  }, true);
})();

/* ============================================================
   NAVBAR — scroll effect + hamburger
   ============================================================ */
function updateNavAuth() {
  const navCta = document.querySelector('.nav-cta');
  const mobileNav = document.getElementById('mobile-nav');

  if (!navCta) return;

  const user = UserProfileStore.getProfile();
  if (Auth.isLoggedIn() && user) {
    const firstName = (user.fullName || 'Traveler').split(' ')[0];
    const avatar = user.avatar || '🧭';
    const isImageAvatar = avatar.startsWith('http') || avatar.startsWith('data:');

    const avatarHtml = isImageAvatar
      ? `<img src="${avatar}" alt="${firstName}" />`
      : `<span>${avatar}</span>`;

    // Desktop Nav CTA with Profile Logo & Dropdown
    navCta.innerHTML = `
      <div class="nav-profile-wrapper" id="nav-profile-wrapper">
        <button class="nav-profile-btn" id="nav-profile-btn" type="button" aria-expanded="false">
          <div class="nav-profile-logo">${avatarHtml}</div>
          <span>${firstName}</span>
          <i class="fa-solid fa-chevron-down" style="font-size:10px; color:var(--clr-text-muted); margin-left:2px;"></i>
        </button>
        <div class="nav-profile-dropdown" id="nav-profile-dropdown">
          <div class="dropdown-user-header">
            <div class="dropdown-user-name">${user.fullName || 'Traveler'}</div>
            <div class="dropdown-user-email">${user.email || ''}</div>
          </div>
          <a href="profile.html" class="dropdown-link">
            <i class="fa-solid fa-user-circle" style="color:var(--clr-primary-light);"></i> My Profile & Travels
          </a>
          <a href="planner.html" class="dropdown-link">
            <i class="fa-solid fa-map-location-dot" style="color:var(--clr-accent);"></i> Trip Planner
          </a>
          <a href="weather.html" class="dropdown-link">
            <i class="fa-solid fa-cloud-sun" style="color:var(--clr-warning);"></i> Live Weather
          </a>
          ${user.role === 'ROLE_ADMIN' ? `
            <a href="admin-dashboard.html" class="dropdown-link">
              <i class="fa-solid fa-gauge-high" style="color:#a855f7;"></i> Admin Dashboard
            </a>
          ` : ''}
          <a href="#" class="dropdown-link logout" id="dropdown-logout-btn">
            <i class="fa-solid fa-arrow-right-from-bracket"></i> Logout
          </a>
        </div>
      </div>
    `;

    // Dropdown toggle logic
    const profileBtn = document.getElementById('nav-profile-btn');
    const profileDropdown = document.getElementById('nav-profile-dropdown');

    if (profileBtn && profileDropdown) {
      profileBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        const isOpen = profileDropdown.classList.toggle('open');
        profileBtn.setAttribute('aria-expanded', String(isOpen));
      });

      document.addEventListener('click', (e) => {
        if (!e.target.closest('#nav-profile-wrapper')) {
          profileDropdown.classList.remove('open');
          profileBtn.setAttribute('aria-expanded', 'false');
        }
      });

      document.getElementById('dropdown-logout-btn')?.addEventListener('click', (e) => {
        e.preventDefault();
        Auth.logout();
      });
    }

    // Update Mobile Nav
    if (mobileNav) {
      const mobileAuthContainer = mobileNav.querySelector('div[style*="display:flex"]');
      if (mobileAuthContainer) {
        mobileAuthContainer.innerHTML = `
          <a href="profile.html" class="btn btn-outline btn-sm" style="flex:1; justify-content:center; gap:6px;">
            <i class="fa-solid fa-user"></i> My Profile
          </a>
          <a href="#" class="btn btn-primary btn-sm" id="mobile-logout-btn" style="flex:1; justify-content:center; gap:6px;">
            <i class="fa-solid fa-arrow-right-from-bracket"></i> Logout
          </a>
        `;
        document.getElementById('mobile-logout-btn')?.addEventListener('click', (e) => {
          e.preventDefault();
          Auth.logout();
        });
      }
    }
  }
}

(function initNavbar() {
  const navbar    = document.getElementById('navbar');
  const hamburger = document.getElementById('hamburger');
  const mobileNav = document.getElementById('mobile-nav');

  if (!navbar) return;

  // Scroll effect
  function onScroll() {
    if (window.scrollY > 20) {
      navbar.classList.add('scrolled');
    } else {
      navbar.classList.remove('scrolled');
    }
  }

  window.addEventListener('scroll', onScroll, { passive: true });
  onScroll();

  // Hamburger toggle
  if (hamburger && mobileNav) {
    hamburger.addEventListener('click', () => {
      const isOpen = hamburger.classList.toggle('open');
      hamburger.setAttribute('aria-expanded', String(isOpen));

      if (isOpen) {
        mobileNav.classList.add('open');
        document.body.style.overflow = 'hidden';
      } else {
        mobileNav.classList.remove('open');
        document.body.style.overflow = '';
      }
    });

    // Close mobile nav on link click
    mobileNav.querySelectorAll('a').forEach(link => {
      link.addEventListener('click', () => {
        hamburger.classList.remove('open');
        hamburger.setAttribute('aria-expanded', 'false');
        mobileNav.classList.remove('open');
        document.body.style.overflow = '';
      });
    });
  }

  // Update nav based on auth state
  updateNavAuth();
})();

/* ============================================================
   SCROLL REVEAL — Robust Intersection Observer + Immediate Fallback
   ============================================================ */
function initScrollReveal() {
  const reveals = document.querySelectorAll('.reveal');
  if (!reveals.length) return;

  function revealAll() {
    reveals.forEach(el => el.classList.add('visible'));
  }

  if (!('IntersectionObserver' in window)) {
    revealAll();
    return;
  }

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible');
        observer.unobserve(entry.target);
      }
    });
  }, {
    threshold: 0.05,
    rootMargin: '0px 0px 80px 0px'
  });

  reveals.forEach(el => observer.observe(el));

  // Immediate safety pass: any elements already near or above fold are visible right away
  function checkVisibleNow() {
    const vh = window.innerHeight || document.documentElement.clientHeight;
    reveals.forEach(el => {
      const rect = el.getBoundingClientRect();
      if (rect.top <= vh + 100) {
        el.classList.add('visible');
      }
    });
  }

  checkVisibleNow();
  setTimeout(checkVisibleNow, 120);
  setTimeout(checkVisibleNow, 450);
  window.addEventListener('scroll', checkVisibleNow, { passive: true, once: true });
}

// Execute scroll reveal as soon as script runs & on DOMContentLoaded
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', initScrollReveal);
} else {
  initScrollReveal();
}

/* ============================================================
   SMOOTH SCROLL for anchor links
   ============================================================ */
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
  anchor.addEventListener('click', function (e) {
    const target = document.querySelector(this.getAttribute('href'));
    if (target) {
      e.preventDefault();
      const navHeight = parseInt(getComputedStyle(document.documentElement).getPropertyValue('--nav-height')) || 70;
      const top = target.getBoundingClientRect().top + window.scrollY - navHeight - 16;
      window.scrollTo({ top, behavior: 'smooth' });
    }
  });
});

// ==== Planner Dynamic Content Initialization ====
document.addEventListener('DOMContentLoaded', () => {
  // Helper to convert rating stars string to numeric value
  function ratingStringToNumber(ratingStr) {
    return (ratingStr.match(/★/g) || []).length;
  }

  const planDestSelect = document.getElementById('plan-dest');
  if (!planDestSelect || typeof DEST_DATA === 'undefined') return;

  function loadDestinationData(destName) {
    const data = DEST_DATA[destName];
    if (!data) return;

    // Hero background image
    const heroBg = document.getElementById('exp-hero-bg');
    if (heroBg) heroBg.style.backgroundImage = `url(${data.heroImg})`;

    // Badges
    const badgeState = document.getElementById('exp-badge-state');
    if (badgeState) badgeState.textContent = data.state;
    const badgeAlt = document.getElementById('exp-badge-alt');
    if (badgeAlt) badgeAlt.textContent = data.altitude;
    const badgeSeason = document.getElementById('exp-badge-season');
    if (badgeSeason) badgeSeason.textContent = `Best: ${data.bestSeason}`;

    // Title & tagline
    const titleEl = document.getElementById('exp-place-title');
    if (titleEl) titleEl.textContent = `${destName} — ${data.tagline}`;
    const taglineEl = document.getElementById('exp-place-tagline');
    if (taglineEl) taglineEl.textContent = data.overview;

    // Description (reuse overview)
    const descEl = document.getElementById('exp-place-desc');
    if (descEl) descEl.textContent = data.overview;

    // Cuisine & photospots
    const cuisineEl = document.getElementById('exp-cuisine');
    if (cuisineEl) cuisineEl.textContent = data.cuisine;
    const photospotsEl = document.getElementById('exp-photospots');
    if (photospotsEl) photospotsEl.textContent = data.photospots;

    // Rating summary
    const ratingText = document.getElementById('exp-rating-text');
    if (ratingText) ratingText.textContent = data.ratingSummary;

    // Gallery
    const galleryContainer = document.getElementById('exp-gallery-grid');
    if (galleryContainer) {
      galleryContainer.innerHTML = data.gallery.map(item => `
        <div class="gallery-item">
          <img src="${item.img}" alt="${item.title}" />
          <div class="gallery-caption">${item.caption}</div>
        </div>
      `).join('');
    }

    // Traveler stories
    renderTravelerStories(data.travelerStories);
  }

  function renderTravelerStories(stories) {
    const container = document.getElementById('traveler-reviews-list');
    if (!container) return;
    container.innerHTML = stories.map(story => {
      const numericRating = ratingStringToNumber(story.rating || '');
      return `
        <div class="traveler-story-card">
          <div class="story-header">
            <div class="traveler-avatar">${story.avatar || story.author.charAt(0)}</div>
            <div>
              <strong>${story.author}</strong><br/>
              <small>${story.style} • ${story.date}</small>
            </div>
          </div>
          <div class="story-rating">${Utils.renderStars(numericRating)}</div>
          <h4>${story.headline}</h4>
          <p>${story.story}</p>
          ${story.tip ? `<div class="story-tip"><i class="fa-solid fa-lightbulb"></i> ${story.tip}</div>` : ''}
        </div>
      `;
    }).join('');
  }

  // Quick‑card button handling
  const recPopularBtn = document.getElementById('btn-rec-popular');
  const recClimateBtn = document.getElementById('btn-rec-climate');
  function setPlannerDestination(dest) {
    if (planDestSelect) {
      planDestSelect.value = dest;
      loadDestinationData(dest);
    }
  }
  recPopularBtn?.addEventListener('click', () => setPlannerDestination(recPopularBtn.dataset.dest));
  recClimateBtn?.addEventListener('click', () => setPlannerDestination(recClimateBtn.dataset.dest));

  // Destination selector change
  planDestSelect?.addEventListener('change', (e) => loadDestinationData(e.target.value));
  // Initial load
  if (planDestSelect) loadDestinationData(planDestSelect.value);

  // Add‑review box toggle
  const toggleReviewBtn = document.getElementById('btn-toggle-add-review');
  const addReviewBox = document.getElementById('add-review-box');
  toggleReviewBtn?.addEventListener('click', () => {
    if (addReviewBox) addReviewBox.style.display = addReviewBox.style.display === 'none' ? 'block' : 'none';
  });

  // Add‑review form submission – pushes into DEST_DATA for current destination
  const addReviewForm = document.getElementById('add-review-form');
  addReviewForm?.addEventListener('submit', (e) => {
    e.preventDefault();
    const author = document.getElementById('rev-author').value.trim();
    const style = document.getElementById('rev-style').value;
    const ratingVal = document.getElementById('rev-rating').value;
    const headline = document.getElementById('rev-headline').value.trim();
    const story = document.getElementById('rev-story').value.trim();
    const tip = document.getElementById('rev-tip').value.trim();
    const dest = planDestSelect?.value;
    if (!dest || !DEST_DATA[dest]) return;
    const newStory = {
      author,
      avatar: author.charAt(0).toUpperCase(),
      style,
      date: new Date().toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' }),
      rating: ratingVal === '5' ? '★★★★★' : ratingVal === '4' ? '★★★★☆' : ratingVal === '3' ? '★★★☆☆' : ratingVal === '2' ? '★★☆☆☆' : '★☆☆☆☆',
      headline,
      story,
      tip: tip || ''
    };
    DEST_DATA[dest].travelerStories.unshift(newStory);
    renderTravelerStories(DEST_DATA[dest].travelerStories);
    addReviewBox.style.display = 'none';
    Toast.success('Your experience was added!');
    addReviewForm.reset();
  });
});

// End of main.js
