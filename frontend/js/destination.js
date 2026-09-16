// destination.js – Dynamically populate destination spot cards and modal details using authentic local photos

const DEST_DATA = {
  ooty: {
    name: "Ooty",
    tagline: "The Queen of Hills – Nilgiri Mountain Range",
    spots: [
      {
        title: "Ooty Lake",
        imageUrl: "images/spots/ooty_lake.jpg",
        shortDesc: "A tranquil 65-acre lake surrounded by eucalyptus groves, perfect for boating and cycling.",
        details: {
          bestTime: "March – June",
          tips: "Rent a paddle-boat early morning to enjoy calm waters and misty mountain reflections.",
          gallery: ["images/spots/ooty_lake.jpg"]
        }
      },
      {
        title: "Doddabetta Peak",
        imageUrl: "images/spots/doddabetta.jpg",
        shortDesc: "The highest vantage point in the Nilgiris at 2,637m, offering breathtaking 360° views.",
        details: {
          bestTime: "October – February",
          tips: "Carry warm jackets as summit winds can be brisk. Visit the telescope house for panoramic views.",
          gallery: ["images/spots/doddabetta.jpg"]
        }
      },
      {
        title: "Government Botanical Garden",
        imageUrl: "images/spots/botanical_garden.jpg",
        shortDesc: "Sprawling 55-acre terraced garden established in 1848 with rare flora and a fossil tree trunk.",
        details: {
          bestTime: "September – November & May (Flower Show)",
          tips: "Take a leisurely walk through the Italian and Fern gardens; early mornings have the best lighting.",
          gallery: ["images/spots/botanical_garden.jpg"]
        }
      },
      {
        title: "Government Rose Garden",
        imageUrl: "images/spots/rose_garden.jpg",
        shortDesc: "India's premier rose sanctuary featuring over 20,000 varieties across five curved terraces.",
        details: {
          bestTime: "November – February",
          tips: "Morning strolls offer peak floral fragrance and vibrant petal colors for photography.",
          gallery: ["images/spots/rose_garden.jpg"]
        }
      },
      {
        title: "Nilgiri Mountain Railway",
        imageUrl: "images/spots/nmr.jpg",
        shortDesc: "UNESCO World Heritage 'Toy Train' chugging through lush forests, bridges, and mountain tunnels.",
        details: {
          bestTime: "Year-round (Best: Oct – Mar)",
          tips: "Book tickets well in advance via IRCTC. Sit on the right side from Mettupalayam for dramatic valley views.",
          gallery: ["images/spots/nmr.jpg"]
        }
      }
    ]
  },
  kodaikanal: {
    name: "Kodaikanal",
    tagline: "The Princess of Hill Stations – Palani Hills",
    spots: [
      {
        title: "Kodaikanal Lake",
        imageUrl: "images/spots/kodaikanal_lake.jpg",
        shortDesc: "An iconic star-shaped man-made lake at 2,133m elevation surrounded by wooded slopes.",
        details: {
          bestTime: "April – June & September – October",
          tips: "Rent tandem bicycles or pedal-boats. The 5km perimeter trail is great for cycling.",
          gallery: ["images/spots/kodaikanal_lake.jpg"]
        }
      },
      {
        title: "Coaker's Walk",
        imageUrl: "images/spots/coakers_walk.jpg",
        shortDesc: "A 1-kilometer winding cliff-edge promenade with breathtaking views over the plains below.",
        details: {
          bestTime: "October – March",
          tips: "Catch the rare 'Brocken Spectre' fog phenomenon if visiting in early misty mornings.",
          gallery: ["images/spots/coakers_walk.jpg"]
        }
      },
      {
        title: "Bryant Park",
        imageUrl: "images/spots/bryant_park.jpg",
        shortDesc: "A masterfully landscaped 20-acre botanical park showcasing hybrids, cacti, and glasshouses.",
        details: {
          bestTime: "May (Annual Flower Show) & Nov – Feb",
          tips: "Located right next to Kodai Lake; ideal picnic spot with colorful flowerbeds.",
          gallery: ["images/spots/bryant_park.jpg"]
        }
      },
      {
        title: "Pillar Rocks",
        imageUrl: "images/spots/pillar_rocks.jpg",
        shortDesc: "Three massive vertical granite cliff boulders towering 122 meters above the valley.",
        details: {
          bestTime: "October – March",
          tips: "Mists frequently roll across the cliffs; wait patiently at the viewpoint for dramatic photo clearings.",
          gallery: ["images/spots/pillar_rocks.jpg"]
        }
      },
      {
        title: "Silver Cascade Falls",
        imageUrl: "images/spots/silver_cascade.jpg",
        shortDesc: "A sparkling 55-meter roadside waterfall formed by the outflow of Kodaikanal Lake.",
        details: {
          bestTime: "July – November (Post-monsoon)",
          tips: "A classic photo stop along the Madurai–Kodaikanal ghat road. Sample roasted spiced corn nearby.",
          gallery: ["images/spots/silver_cascade.jpg"]
        }
      }
    ]
  },
  munnar: {
    name: "Munnar",
    tagline: "The Tea Garden Capital – Western Ghats",
    spots: [
      {
        title: "Tea Plantations",
        imageUrl: "images/spots/tea_plantations.jpg",
        shortDesc: "Endless rolling green hills blanketed in manicured tea shrubs stretching to the horizon.",
        details: {
          bestTime: "September – March",
          tips: "Visit the Tata Tea Museum at Nallathanni Estate to observe orthodox tea processing.",
          gallery: ["images/spots/tea_plantations.jpg"]
        }
      },
      {
        title: "Eravikulam National Park",
        imageUrl: "images/spots/eravikulam.jpg",
        shortDesc: "High-altitude sanctuary home to the endangered Nilgiri Tahr and the rare Neelakurinji flower.",
        details: {
          bestTime: "September – February (Park closes during calving Feb-Mar)",
          tips: "Pre-book tickets online to skip long shuttle bus queues at the entrance gate.",
          gallery: ["images/spots/eravikulam.jpg"]
        }
      },
      {
        title: "Mattupetty Dam",
        imageUrl: "images/spots/mattupetty.jpg",
        shortDesc: "A gravity dam with a serene reservoir nestled between tea gardens and shola woodlands.",
        details: {
          bestTime: "October – May",
          tips: "Enjoy speedboat rides across the reservoir; look out for wild elephants drinking by the shore.",
          gallery: ["images/spots/mattupetty.jpg"]
        }
      },
      {
        title: "Anamudi Peak",
        imageUrl: "images/spots/anamudi.jpg",
        shortDesc: "The rooftop of South India standing at 2,695m, dominating the skyline of the Western Ghats.",
        details: {
          bestTime: "November – February",
          tips: "Viewable from inside Eravikulam National Park; trekking requires special forest department clearance.",
          gallery: ["images/spots/anamudi.jpg"]
        }
      },
      {
        title: "Attukal Waterfalls",
        imageUrl: "images/spots/attukal.jpg",
        shortDesc: "A roaring cascade tucked between dense jungle slopes and rolling tea terraces.",
        details: {
          bestTime: "July – October (Monsoon & post-monsoon)",
          tips: "Cross the suspension footbridge for the best vantage point of the cascading water.",
          gallery: ["images/spots/attukal.jpg"]
        }
      }
    ]
  },
  mysore: {
    name: "Mysore",
    tagline: "The City of Palaces – Royal Heritage",
    spots: [
      {
        title: "Mysore Palace",
        imageUrl: "images/spots/mysore_palace.jpg",
        shortDesc: "An Indo-Saracenic architectural masterpiece illuminated by nearly 100,000 bulbs on festive nights.",
        details: {
          bestTime: "October (Dasara) & October – March",
          tips: "Don't miss the evening illumination on Sundays and public holidays (7:00 PM – 8:00 PM).",
          gallery: ["images/spots/mysore_palace.jpg"]
        }
      },
      {
        title: "Chamundeshwari Temple",
        imageUrl: "images/spots/chamundeshwari.jpg",
        shortDesc: "Hilltop temple atop Chamundi Hills venerating Goddess Chamundi, with a huge Nandi monolith.",
        details: {
          bestTime: "September – March",
          tips: "Visit the monolithic Nandi statue halfway up the hill. Early mornings offer fewer queues.",
          gallery: ["images/spots/chamundeshwari.jpg"]
        }
      },
      {
        title: "Brindavan Gardens",
        imageUrl: "images/spots/brindavan_gardens.jpg",
        shortDesc: "Symmetrical terraced gardens alongside the KRS Dam featuring musical dancing fountains.",
        details: {
          bestTime: "October – February",
          tips: "Arrive in the late afternoon to explore the gardens and stay for the lighted musical fountain show at dusk.",
          gallery: ["images/spots/brindavan_gardens.jpg"]
        }
      },
      {
        title: "Mysore Zoo",
        imageUrl: "images/spots/mysore_zoo.jpg",
        shortDesc: "One of India's oldest and best-kept zoological gardens spanning 157 lush acres.",
        details: {
          bestTime: "October – March (Closed on Tuesdays)",
          tips: "Battery-operated vehicles are available for seniors. Morning hours are best to spot active animals.",
          gallery: ["images/spots/mysore_zoo.jpg"]
        }
      },
      {
        title: "Jaganmohan Palace & Art Gallery",
        imageUrl: "images/spots/jaganmohan_palace.jpg",
        shortDesc: "Former royal residence transformed into a museum housing original Raja Ravi Varma masterpieces.",
        details: {
          bestTime: "Year-round (8:30 AM – 5:30 PM)",
          tips: "Admire the famous 'Glow of Hope' (Woman with Lamp) painting in a specially darkened display room.",
          gallery: ["images/spots/jaganmohan_palace.jpg"]
        }
      }
    ]
  },
  coimbatore: {
    name: "Coimbatore",
    tagline: "The Textile & Cultural Hub of Tamil Nadu",
    spots: [
      {
        title: "Marudamalai Murugan Temple",
        imageUrl: "images/spots/marudamalai.jpg",
        shortDesc: "Ancient 12th-century hill shrine dedicated to Lord Murugan, nestled in the scenic Western Ghats.",
        details: {
          bestTime: "September – March",
          tips: "Take the temple shuttle bus or climb the gentle stone steps for panoramic valley vistas.",
          gallery: ["images/spots/marudamalai.jpg"]
        }
      },
      {
        title: "Isha Yoga Center & Adiyogi",
        imageUrl: "images/spots/isha_center.jpg",
        shortDesc: "Spiritual sanctuary featuring the colossal 112-foot Guinness World Record Adiyogi Shiva sculpture.",
        details: {
          bestTime: "September – March",
          tips: "Attend the Divya Darshanam 3D light & sound laser show projected onto the Adiyogi bust at 7:00 PM.",
          gallery: ["images/spots/isha_center.jpg"]
        }
      },
      {
        title: "Siruvani Waterfalls",
        imageUrl: "images/spots/siruvani_falls.jpg",
        shortDesc: "Picturesque waterfalls surrounded by dense forests, known for possessing the world's second sweetest water.",
        details: {
          bestTime: "August – December",
          tips: "Requires forest department pass. The trek through pristine forest makes it a refreshing nature retreat.",
          gallery: ["images/spots/siruvani_falls.jpg"]
        }
      },
      {
        title: "Black Thunder Water Theme Park",
        imageUrl: "images/spots/black_thunder.jpg",
        shortDesc: "Sprawling 75-acre water and amusement park situated at the scenic foothills of the Nilgiris.",
        details: {
          bestTime: "March – June & September – December",
          tips: "Features over 40 water rides and wave pools; ideal for families and thrilling day outings.",
          gallery: ["images/spots/black_thunder.jpg"]
        }
      },
      {
        title: "Anamalai Tiger Reserve",
        imageUrl: "images/spots/anamalai_reserve.jpg",
        shortDesc: "Vast ecological hotspot with diverse wildlife including tigers, elephants, leopards, and hornbills.",
        details: {
          bestTime: "December – June",
          tips: "Book morning jeep safaris from Topslip for the highest chance of seeing wild elephants and deer.",
          gallery: ["images/spots/anamalai_reserve.jpg"]
        }
      }
    ]
  },
  chennai: {
    name: "Chennai",
    tagline: "The Gateway of South India – Coastal Heritage",
    spots: [
      {
        title: "Marina Beach",
        imageUrl: "images/spots/marina_beach.jpg",
        shortDesc: "The world's second-longest natural urban beach stretching 13 kilometers along the Bay of Bengal.",
        details: {
          bestTime: "November – February (Mornings & Evenings)",
          tips: "Enjoy the sea breeze, fresh sundal and fish fry from beach vendors. Visit the historic lighthouse for aerial views.",
          gallery: ["images/spots/marina_beach.jpg"]
        }
      },
      {
        title: "Kapaleeshwarar Temple",
        imageUrl: "images/spots/kapaleeshwarar.jpg",
        shortDesc: "Ancient 7th-century Dravidian temple in Mylapore with an intricately carved, vibrant gopuram.",
        details: {
          bestTime: "October – March (Morning 6:00 AM – 12:30 PM, Evening 4:00 PM – 9:00 PM)",
          tips: "Witness the evening aarti and take a peaceful walk around the sacred temple tank (kulam).",
          gallery: ["images/spots/kapaleeshwarar.jpg"]
        }
      },
      {
        title: "Fort St. George",
        imageUrl: "images/spots/fort_st_george.jpg",
        shortDesc: "The first English fortress in India, built in 1644, housing St. Mary's Church and the Fort Museum.",
        details: {
          bestTime: "October – March (Closed on Fridays)",
          tips: "The Fort Museum showcases colonial-era coins, weapons, uniform medals, and original letters.",
          gallery: ["images/spots/fort_st_george.jpg"]
        }
      },
      {
        title: "Government Museum, Chennai",
        imageUrl: "images/spots/chennai_museum.jpg",
        shortDesc: "India's second oldest museum complex housing world-famous Chola bronzes and Amaravati sculptures.",
        details: {
          bestTime: "Year-round (9:30 AM – 5:00 PM, Closed on Fridays)",
          tips: "The Bronze Gallery with the Nataraja bronze and the Connemara Public Library are essential highlights.",
          gallery: ["images/spots/chennai_museum.jpg"]
        }
      },
      {
        title: "Edward Elliot's Beach (Besant Nagar)",
        imageUrl: "images/spots/elliots_beach.jpg",
        shortDesc: "A cleaner, quieter sandy beach crowned by the historic Karl Schmidt Memorial monument.",
        details: {
          bestTime: "November – February",
          tips: "Surrounded by popular seaside cafes and eateries; an ideal spot for evening relaxation and sunsets.",
          gallery: ["images/spots/elliots_beach.jpg"]
        }
      }
    ]
  }
};

function qs(sel) { return document.querySelector(sel); }

function createSpotCard(spot, destKey) {
  const card = document.createElement('div');
  card.className = 'spot-card';

  const img = document.createElement('img');
  img.src = spot.imageUrl;
  img.alt = spot.title;
  img.loading = 'lazy';
  card.appendChild(img);

  const body = document.createElement('div');
  body.className = 'spot-body';

  const title = document.createElement('h4');
  title.textContent = spot.title;
  body.appendChild(title);

  const desc = document.createElement('p');
  desc.textContent = spot.shortDesc;
  body.appendChild(desc);

  const btn = document.createElement('button');
  btn.className = 'spot-btn';
  btn.textContent = 'View Details';
  btn.addEventListener('click', () => openModal(spot, destKey));
  body.appendChild(btn);

  card.appendChild(body);
  return card;
}

function openModal(spot, destKey) {
  qs('#modal-title').textContent = spot.title;
  qs('#modal-desc').textContent = spot.shortDesc;
  qs('#modal-besttime').textContent = spot.details.bestTime;
  qs('#modal-tips').textContent = spot.details.tips;

  const carousel = qs('#modal-carousel');
  carousel.innerHTML = '';
  if (spot.details.gallery && spot.details.gallery.length) {
    spot.details.gallery.forEach((url, idx) => {
      const img = document.createElement('img');
      img.src = url;
      img.alt = `${spot.title} photo ${idx + 1}`;
      carousel.appendChild(img);
    });
  }

  const planUrl = new URL('planner.html', location.href);
  planUrl.searchParams.set('dest', destKey);
  planUrl.searchParams.set('spot', spot.title);
  qs('#plan-button').href = planUrl.toString();

  const modal = qs('#spot-modal');
  modal.classList.add('active');
  modal.setAttribute('aria-hidden', 'false');
}

function closeModal() {
  const modal = qs('#spot-modal');
  modal.classList.remove('active');
  modal.setAttribute('aria-hidden', 'true');
}

function init() {
  const params = new URLSearchParams(window.location.search);
  const rawDest = (params.get('dest') || 'ooty').trim().toLowerCase();
  
  // Normalization map to support Ooty, OOTY, ooty, etc.
  const aliasMap = {
    'ooty': 'ooty',
    'kodaikanal': 'kodaikanal',
    'kodai': 'kodaikanal',
    'munnar': 'munnar',
    'mysore': 'mysore',
    'mysuru': 'mysore',
    'coimbatore': 'coimbatore',
    'chennai': 'chennai'
  };
  
  const destKey = aliasMap[rawDest] || rawDest;
  const data = DEST_DATA[destKey];

  if (!data) {
    qs('#dest-name').textContent = 'Destination Not Found';
    qs('#dest-tagline').textContent = 'Please select Ooty, Kodaikanal, Munnar, Mysore, Coimbatore, or Chennai from Explore.';
    return;
  }

  qs('#dest-name').textContent = data.name;
  qs('#dest-tagline').textContent = data.tagline;

  const gallery = qs('#spot-gallery');
  gallery.innerHTML = '';
  data.spots.forEach(spot => gallery.appendChild(createSpotCard(spot, destKey)));

  qs('#modal-close').addEventListener('click', closeModal);
  qs('#spot-modal').addEventListener('click', e => {
    if (e.target === qs('#spot-modal')) closeModal();
  });
}

document.addEventListener('DOMContentLoaded', init);
