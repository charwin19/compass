package com.smarttourism.config;

import com.smarttourism.entity.*;
import com.smarttourism.entity.User.Role;
import com.smarttourism.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the database with realistic sample data on first boot.
 * Set app.seeder.enabled=false in production after initial seeding.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final DestinationRepository destinationRepo;
    private final PlaceRepository       placeRepo;
    private final HiddenPlaceRepository hiddenPlaceRepo;
    private final GuideRepository       guideRepo;
    private final HotelRepository       hotelRepo;
    private final RouteRepository       routeRepo;
    private final UserRepository        userRepo;
    private final PasswordEncoder       passwordEncoder;

    @Value("${app.seeder.enabled:true}")
    private boolean seederEnabled;

    @Override
    public void run(String... args) {
        if (!seederEnabled) {
            log.info("DataInitializer: seeder disabled — skipping.");
            return;
        }
        if (destinationRepo.count() > 0) {
            log.info("DataInitializer: data already exists — skipping.");
            return;
        }

        log.info("DataInitializer: seeding sample data...");

        seedAdminUser();
        seedOoty();
        seedKodaikanal();
        seedMunnar();
        seedMysore();
        seedCoimbatore();
        seedChennai();

        log.info("DataInitializer: sample data seeded successfully.");
    }

    // ================================================================
    // ADMIN USER
    // ================================================================
    private void seedAdminUser() {
        User admin = User.builder()
                .fullName("Admin User")
                .email("admin@smarttourism.com")
                .phone("9999999999")
                .password(passwordEncoder.encode("Admin@1234"))
                .role(Role.ADMIN)
                .build();
        userRepo.save(admin);

        User demo = User.builder()
                .fullName("Demo Traveler")
                .email("demo@smarttourism.com")
                .phone("9876543210")
                .password(passwordEncoder.encode("Demo@1234"))
                .role(Role.USER)
                .build();
        userRepo.save(demo);

        log.info("  → Admin and demo user created.");
    }

    // ================================================================
    // OOTY
    // ================================================================
    private void seedOoty() {
        Destination ooty = Destination.builder()
                .name("Ooty")
                .state("Tamil Nadu")
                .description("The Queen of Hill Stations nestled in the Nilgiri mountains at 2,240 m. Famous for its lush tea gardens, colonial architecture, Botanical Garden and scenic toy train.")
                .imageUrl("https://images.unsplash.com/photo-1586763894767-adb2dbc26d75?w=800&q=80")
                .rating(4.8)
                .reviewCount(2400)
                .category("Hill Station")
                .bestTimeToVisit("April – June, September – November")
                .build();
        ooty = destinationRepo.save(ooty);

        // Places
        placeRepo.saveAll(List.of(
                place("Government Botanical Garden", "A 22-hectare garden with 2,200+ plant species including a 20-million-year-old fossil tree.", "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600", "Ooty Town", 2.0, 50, 1, "09:00", "18:30", "BUDGET", "family,solo,couple", ooty),
                place("Ooty Lake", "A scenic artificial lake built in 1825 — pedal boating, rowing and lakeside walks amid eucalyptus groves.", "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600", "Garden Road, Ooty", 1.5, 30, 1, "08:30", "18:00", "BUDGET", "family,couple", ooty),
                place("Doddabetta Peak", "The highest peak in the Nilgiris at 2,637 m. Panoramic 360° views of the mountains and a popular telescope house.", "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600", "11 km from Ooty", 2.0, 10, 2, "07:00", "18:00", "BUDGET", "solo,adventure,family", ooty),
                place("Ooty Tea Museum", "Understand the journey from tea leaf to cup — live factory tour, interactive museum and direct purchase.", "https://images.unsplash.com/photo-1559181567-c3190bfbf3a8?w=600", "Doddabetta Road, Ooty", 1.5, 50, 2, "09:00", "17:00", "STANDARD", "family,solo", ooty),
                place("Rose Garden", "Asia's largest rose garden with 2,000+ varieties spread over 4 hectares on the slopes of Elk Hill.", "https://images.unsplash.com/photo-1490750967868-88df5691cc13?w=600", "Vijayaraghava Road, Ooty", 1.0, 30, 1, "08:00", "18:30", "BUDGET", "couple,family", ooty),
                place("Nilgiri Mountain Railway", "UNESCO World Heritage toy train from Mettupalayam to Ooty — a breathtaking steam journey through 16 tunnels.", "https://images.unsplash.com/photo-1476362555312-ab9e108a0b7e?w=600", "Ooty Railway Station", 4.0, 0, 3, "07:10", "11:30", "STANDARD", "family,couple,solo", ooty)
        ));

        // Hidden Places
        hiddenPlaceRepo.saveAll(List.of(
                hidden("Avalanche Lake", "Ooty", "A pristine glacial lake 28 km from Ooty, surrounded by dense shola forests. Almost no tourists.", "If you want solitude and raw Nilgiri beauty — this is it. Best trout fishing spot too.", "October – March, early morning", 3.0, "https://images.unsplash.com/photo-1534949104046-d9142fa4bbb2?w=600", "MODERATE", ooty),
                hidden("Toda Huts of Ooty", "Ooty outskirts", "Ancient barrel-shaped stone huts of the indigenous Toda tribe, unchanged for centuries.", "Rare glimpse into a living Neolithic culture. The buffaloes, embroidery, and rituals are unlike anything else.", "Year-round", 1.5, "https://images.unsplash.com/photo-1571847140471-1d7766e825ea?w=600", "EASY", ooty),
                hidden("Mukurthi National Park", "80 km from Ooty", "UNESCO World Heritage biosphere — shola grasslands, nilgiri tahr and rare Nilgiri marten.", "Off-limits to most — requires special permit. The most pristine ecosystem in the Nilgiris.", "October – May", 5.0, "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600", "HARD", ooty)
        ));

        // Guides
        guideRepo.saveAll(List.of(
                guide("Arun Kumar", "Born and raised in Ooty, Arun has guided over 500 trips. Expert in tribal culture, tea estates and hidden treks.", "https://api.dicebear.com/7.x/avataaars/svg?seed=Arun", 5, "Tamil, English, Hindi", 4.7, 312, "XXXXXX3421", 1500, "Wildlife, Cultural, Tea Heritage", ooty),
                guide("Meena Devi", "Female guide specialising in family and women-only tours. Deep knowledge of local cuisine and shopping.", "https://api.dicebear.com/7.x/avataaars/svg?seed=Meena", 3, "Tamil, English", 4.5, 145, "XXXXXX7823", 1200, "Family, Food, Shopping", ooty)
        ));

        // Hotels
        hotelRepo.saveAll(List.of(
                hotel("Ooty Breeze Homestay", "Cozy family-run homestay with garden views, home-cooked Nilgiri meals and warm hospitality.", "https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=600", 4.2, 89, 1200, 2.5, "BUDGET", "WiFi, Parking, Home-cooked meals, Garden view", "42 Garden Road, Ooty", ooty),
                hotel("Nilgiri Residency", "3-star hotel with spacious rooms, restaurant, bonfire area and guided garden walks.", "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=600", 4.4, 234, 2500, 1.2, "STANDARD", "WiFi, AC, Restaurant, Bar, Bonfire, Parking", "21 Charing Cross, Ooty", ooty),
                hotel("The Fern Hill — Ooty", "Heritage luxury property in a 170-year-old colonial bungalow, set amid 50 acres of tea estates.", "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=600", 4.8, 512, 8500, 3.0, "LUXURY", "WiFi, AC, Restaurant, Pool, Spa, Heritage walks, Room service", "Fern Hill Road, Ooty", ooty)
        ));

        // Route
        routeRepo.save(Route.builder()
                .startLocation("Coimbatore Airport / Railway Station")
                .endLocation("Ooty (Udhagamandalam)")
                .distanceKm(88.0)
                .estimatedTimeHours(3.5)
                .roadType("State Highway 15 + Mountain Road")
                .roadCondition("GOOD")
                .importantStops("Mettupalayam (36 km), Coonoor (17 km from Ooty)")
                .alternativeRoute("Take Nilgiri Mountain Railway from Mettupalayam — 5 hrs, scenic UNESCO heritage journey")
                .travelMode("ROAD")
                .destination(ooty)
                .build());

        log.info("  → Ooty seeded.");
    }

    // ================================================================
    // KODAIKANAL
    // ================================================================
    private void seedKodaikanal() {
        Destination kodi = Destination.builder()
                .name("Kodaikanal")
                .state("Tamil Nadu")
                .description("The Princess of Hill Stations at 2,133 m — famous for its star-shaped lake, pine forests, Coaker's Walk, and Silver Cascade waterfall.")
                .imageUrl("https://images.unsplash.com/photo-1604999333679-b86d54738315?w=800&q=80")
                .rating(4.7)
                .reviewCount(1800)
                .category("Hill Station")
                .bestTimeToVisit("April – June, October – December")
                .build();
        kodi = destinationRepo.save(kodi);

        placeRepo.saveAll(List.of(
                place("Kodaikanal Lake", "The star-shaped 24-hectare lake at the heart of Kodaikanal. Cycling, boating and lakeside walks.", "https://images.unsplash.com/photo-1534949104046-d9142fa4bbb2?w=600", "Lake Road, Kodaikanal", 2.0, 0, 1, "06:00", "20:00", "BUDGET", "family,couple,solo", kodi),
                place("Coaker's Walk", "A stunning 1 km pedestrian path along a cliff edge offering misty valley views and occasional cloud-walking experience.", "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600", "P.T. Rajan Salai, Kodaikanal", 1.0, 15, 1, "07:30", "19:00", "BUDGET", "solo,couple", kodi),
                place("Pillar Rocks", "Three massive 122-metre-high granite pillars rising from dense forest — iconic Kodaikanal viewpoint.", "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=600", "Pillar Rocks Road, 8 km from lake", 1.5, 15, 2, "09:00", "17:30", "BUDGET", "family,solo,adventure", kodi),
                place("Silver Cascade Waterfall", "120-foot waterfall formed by the overflow of Kodaikanal Lake. Best visited after monsoon rains.", "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600", "Kodaikanal-Palani Highway, 8 km", 1.0, 0, 2, "06:00", "19:00", "BUDGET", "family,couple", kodi),
                place("Bryant Park", "A botanical garden with over 700 plant species, orchid house, topiary and flower shows in summer.", "https://images.unsplash.com/photo-1490750967868-88df5691cc13?w=600", "Lake Road, Kodaikanal", 1.5, 30, 1, "08:30", "18:30", "BUDGET", "family", kodi)
        ));

        hiddenPlaceRepo.saveAll(List.of(
                hidden("Berijam Lake", "Kodaikanal", "A pristine reservoir 21 km from Kodaikanal, deep inside a forest reserve. Entry strictly limited.", "One of the cleanest lakes in Asia. The forest road alone is magical. Requires forest department permit.", "October – March", 3.0, "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600", "MODERATE", kodi),
                hidden("Dolphin's Nose", "Kodaikanal", "A slender rock ledge jutting out like a dolphin's nose — 360° valley view 6 km from town.", "Fewer crowds than Pillar Rocks. A 3 km trek through pine and eucalyptus forests is rewarding.", "October – May, early morning", 2.5, "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=600", "EASY", kodi)
        ));

        guideRepo.save(guide("Rajan Pillai", "Third-generation Kodaikanal guide with deep knowledge of Palani hills ecology and tribal communities.", "https://api.dicebear.com/7.x/avataaars/svg?seed=Rajan", 8, "Tamil, English, Malayalam", 4.8, 456, "XXXXXX9012", 1400, "Trekking, Heritage, Birding", kodi));

        hotelRepo.saveAll(List.of(
                hotel("Kodai Pine Cottages", "Pine-surrounded private cottages with fireplace and mountain views. Perfect for couples.", "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=600", 4.3, 123, 1800, 1.5, "STANDARD", "WiFi, Fireplace, Kitchen, Parking, Garden", "Club Road, Kodaikanal", kodi),
                hotel("Carlton Hotel Kodaikanal", "Heritage lakeside luxury hotel with stunning views of the lake and boathouse access.", "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=600", 4.7, 678, 7500, 0.2, "LUXURY", "WiFi, AC, Restaurant, Pool, Spa, Boathouse, Gym", "Lake Road, Kodaikanal", kodi)
        ));

        routeRepo.save(Route.builder()
                .startLocation("Madurai Junction")
                .endLocation("Kodaikanal")
                .distanceKm(120.0)
                .estimatedTimeHours(4.0)
                .roadType("National Highway 44 + Mountain Road")
                .roadCondition("GOOD")
                .importantStops("Dindigul (75 km), Kodaikanal Road (10 km)", "Route via Palani offers detour to Palani Murugan Temple")
                .alternativeRoute("Train to Kodaikanal Road station + taxi (10 km uphill drive)")
                .travelMode("ROAD")
                .destination(kodi)
                .build());

        log.info("  → Kodaikanal seeded.");
    }

    // ================================================================
    // MUNNAR
    // ================================================================
    private void seedMunnar() {
        Destination munnar = Destination.builder()
                .name("Munnar")
                .state("Kerala")
                .description("Emerald tea capital of India at 1,600 m — rolling tea estates, misty mountains, rare Neelakurinji flowers (blooms every 12 years) and pristine wildlife sanctuaries.")
                .imageUrl("https://images.unsplash.com/photo-1593693397690-362cb9666fc2?w=800&q=80")
                .rating(4.9)
                .reviewCount(3100)
                .category("Hill Station")
                .bestTimeToVisit("September – March")
                .build();
        munnar = destinationRepo.save(munnar);

        placeRepo.saveAll(List.of(
                place("Tea Museum (KDHP)", "Live demonstration of tea processing from plucking to packaging. Shop for some of the finest Munnar teas.", "https://images.unsplash.com/photo-1559181567-c3190bfbf3a8?w=600", "Nallathanni, Munnar", 2.0, 75, 1, "09:00", "17:00", "STANDARD", "family,solo", munnar),
                place("Eravikulam National Park", "Home to the endangered Nilgiri tahr — India's most accessible mountain goat. Stunning Rajamala viewpoints.", "https://images.unsplash.com/photo-1593693397690-362cb9666fc2?w=600", "Rajamala, 13 km from Munnar", 3.0, 130, 2, "07:30", "16:00", "STANDARD", "wildlife,adventure,family", munnar),
                place("Mattupetty Dam & Lake", "A serene dam built in 1940 — boating on calm waters with tea estate backdrops and elephant sightings.", "https://images.unsplash.com/photo-1534949104046-d9142fa4bbb2?w=600", "13 km from Munnar", 2.5, 0, 1, "09:00", "17:00", "BUDGET", "family,couple", munnar),
                place("Attukal Waterfalls", "A spectacular multi-tiered waterfall 9 km from Munnar, flowing through dense spice and tea plantations.", "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600", "9 km from Munnar on Munnar-Bodimettu road", 1.5, 0, 2, "06:00", "18:00", "BUDGET", "solo,couple,adventure", munnar)
        ));

        hiddenPlaceRepo.save(hidden("Chinnakanal Waterfalls", "Chinnakanal, 23 km", "A hidden 2000-foot waterfall, the second-highest in Kerala, known only to locals. No ticket counters.", "Almost no tourists. Lush cardamom and tea estates on the 2 km walk. A photographer's dream.", "July – November", 4.0, "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600", "MODERATE", munnar));

        guideRepo.save(guide("Suresh Nair", "Munnar native and wildlife enthusiast. Expert in Neelakurinji tracking, birding and tea estate history.", "https://api.dicebear.com/7.x/avataaars/svg?seed=Suresh", 7, "Malayalam, Tamil, English", 4.9, 389, "XXXXXX5678", 1600, "Wildlife, Birding, Tea Heritage", munnar));

        hotelRepo.saveAll(List.of(
                hotel("Tea Nest Munnar", "Cozy guesthouse inside a working tea estate with organic meals and guided plucking sessions.", "https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=600", 4.5, 167, 1500, 2.0, "BUDGET", "WiFi, Tea estate view, Organic meals, Guided tours", "Pothamedu, Munnar", munnar),
                hotel("Windermere Estate", "Award-winning boutique resort in a 10-acre cardamom-tea-coffee plantation. Treehouse suites available.", "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=600", 4.9, 890, 9000, 4.0, "LUXURY", "WiFi, Pool, Spa, Restaurant, Plantation walks, Treehouse, Bonfire", "Pothamedu Estate, Munnar", munnar)
        ));

        routeRepo.save(Route.builder()
                .startLocation("Kochi (Ernakulam)")
                .endLocation("Munnar")
                .distanceKm(130.0)
                .estimatedTimeHours(4.5)
                .roadType("State Highway 23")
                .roadCondition("GOOD")
                .importantStops("Aluva (20 km), Kothamangalam (63 km), Adimali (113 km)")
                .alternativeRoute("Via Palakkad (longer but better roads). Avoid monsoon nights — landslide risk.")
                .travelMode("ROAD")
                .destination(munnar)
                .build());

        log.info("  → Munnar seeded.");
    }

    // ================================================================
    // MYSORE
    // ================================================================
    private void seedMysore() {
        Destination mysore = Destination.builder()
                .name("Mysore")
                .state("Karnataka")
                .description("The City of Palaces — a royal heritage destination famed for the Mysore Palace, vibrant Dasara festival, silk sarees, sandalwood and Chamundi Hills.")
                .imageUrl("https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=800&q=80")
                .rating(4.6)
                .reviewCount(2000)
                .category("Heritage")
                .bestTimeToVisit("October (Dasara), November – February")
                .build();
        mysore = destinationRepo.save(mysore);

        placeRepo.saveAll(List.of(
                place("Mysore Palace", "One of India's most visited monuments — 76,000 bulbs illuminate it on Sundays. Royal heritage at its finest.", "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=600", "Sayyaji Rao Road, Mysore", 2.5, 200, 1, "10:00", "17:30", "STANDARD", "family,history,culture", mysore),
                place("Chamundi Hills", "A sacred hill 13 km from Mysore with the Chamundeshwari Temple and panoramic city views. 1000 steps!", "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600", "13 km from Mysore city", 2.5, 0, 1, "06:00", "21:00", "BUDGET", "spiritual,family", mysore),
                place("Brindavan Gardens", "Musical fountain gardens below KRS Dam — illuminated dancing fountains each evening.", "https://images.unsplash.com/photo-1490750967868-88df5691cc13?w=600", "KRS Road, 19 km from Mysore", 2.0, 50, 2, "06:30", "21:00", "BUDGET", "family,couple", mysore),
                place("Mysore Zoo", "One of India's oldest and finest zoological parks — established in 1892. Home to gorillas, giraffes and white tigers.", "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600", "Indira Gandhi Road, Mysore", 3.0, 100, 2, "08:30", "17:30", "BUDGET", "family", mysore)
        ));

        hiddenPlaceRepo.save(hidden("Talakadu Sand Dunes", "Talakadu, 50 km from Mysore", "An ancient town buried in sand dunes on the Cauvery riverbank — a geological mystery of Karnataka.", "Five submerged Shiva temples emerging from sand. A surreal, crowd-free experience unlike any other in South India.", "November – February", 3.0, "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=600", "EASY", mysore));

        guideRepo.save(guide("Vishwanath Rao", "Royal family descendant and heritage expert. Speaks Kannada, Hindi and English fluently.", "https://api.dicebear.com/7.x/avataaars/svg?seed=Vishwanath", 12, "Kannada, English, Hindi", 4.6, 521, "XXXXXX2345", 1300, "Heritage, Royal History, Architecture", mysore));

        hotelRepo.saveAll(List.of(
                hotel("Mysore Regaalis", "Comfortable 3-star hotel in the heart of Mysore — walking distance from the Palace.", "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=600", 4.3, 345, 2200, 0.8, "STANDARD", "WiFi, AC, Restaurant, Parking, Room service", "Nazarbad Main Road, Mysore", mysore),
                hotel("Lalitha Mahal Palace Hotel", "A heritage palace hotel converted from the Maharaja's guest house — grand architecture, royal experience.", "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=600", 4.7, 430, 11000, 3.0, "LUXURY", "WiFi, Pool, Spa, Restaurant, Tennis court, Heritage property, Garden", "T Narasipur Road, Mysore", mysore)
        ));

        log.info("  → Mysore seeded.");
    }

    // ================================================================
    // COIMBATORE
    // ================================================================
    private void seedCoimbatore() {
        Destination cbe = Destination.builder()
                .name("Coimbatore")
                .state("Tamil Nadu")
                .description("Gateway to the Nilgiris and major industrial city — balanced with Isha Yoga Center, Marudamalai temple, Velliangiri mountains and proximity to Ooty and Kodaikanal.")
                .imageUrl("https://images.unsplash.com/photo-1518623489648-a173ef7824f3?w=800&q=80")
                .rating(4.4)
                .reviewCount(1200)
                .category("City & Nature")
                .bestTimeToVisit("November – February")
                .build();
        cbe = destinationRepo.save(cbe);

        placeRepo.saveAll(List.of(
                place("Isha Yoga Center", "World-famous meditation and yoga center at the foothills of Velliangiri mountains. Visit Dhyanalinga and Adiyogi.", "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600", "Velliangiri Foothills, 27 km from Coimbatore", 3.0, 0, 1, "06:00", "20:00", "BUDGET", "spiritual,solo,family", cbe),
                place("Marudamalai Temple", "Ancient hilltop Murugan temple 12 km from city — sacred, scenic and surrounded by forest.", "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=600", "Marudamalai Hills, 12 km", 1.5, 0, 1, "05:00", "21:00", "BUDGET", "spiritual,family", cbe),
                place("Gedee Car Museum", "Unique private museum with 70+ vintage cars, motorcycles and aircraft — a must for enthusiasts.", "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600", "Avanashi Road, Coimbatore", 2.0, 50, 2, "09:30", "17:30", "STANDARD", "family,solo,interest", cbe)
        ));

        guideRepo.save(guide("Karthik Selvam", "Coimbatore local expert specializing in temple tourism and textile industry tours.", "https://api.dicebear.com/7.x/avataaars/svg?seed=Karthik", 4, "Tamil, English", 4.4, 187, "XXXXXX6789", 1100, "Temple, Industrial, Day-trips", cbe));

        hotelRepo.saveAll(List.of(
                hotel("Hotel Arcadia", "Budget-friendly city hotel with clean rooms, near Gandhipuram bus stand.", "https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=600", 3.8, 234, 900, 1.5, "BUDGET", "WiFi, AC, Restaurant, Parking", "Gandhipuram, Coimbatore", cbe),
                hotel("Taj Coimbatore", "5-star luxury in the city's commercial hub with infinity pool, spa and rooftop dining.", "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=600", 4.8, 789, 9500, 2.0, "LUXURY", "WiFi, Pool, Spa, Restaurant, Bar, Gym, Rooftop dining", "Avinashi Road, Coimbatore", cbe)
        ));

        log.info("  → Coimbatore seeded.");
    }

    // ================================================================
    // CHENNAI
    // ================================================================
    private void seedChennai() {
        Destination chennai = Destination.builder()
                .name("Chennai")
                .state("Tamil Nadu")
                .description("Cultural capital of South India — Marina Beach (world's 2nd longest), Kapaleeshwarar temple, Dravidian architecture, Carnatic music, diverse street food and modern shopping.")
                .imageUrl("https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=800&q=80")
                .rating(4.5)
                .reviewCount(2700)
                .category("Coastal City")
                .bestTimeToVisit("November – February")
                .build();
        chennai = destinationRepo.save(chennai);

        placeRepo.saveAll(List.of(
                place("Marina Beach", "World's second-longest urban beach at 13 km. Sunrise walks, bhaji stalls, filter coffee and local energy.", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600", "Marina, Chennai", 2.0, 0, 1, "05:00", "21:00", "BUDGET", "family,solo,couple", chennai),
                place("Kapaleeshwarar Temple", "7th century Dravidian masterpiece in Mylapore — intricate gopuram, sacred tank and morning rituals.", "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=600", "Mylapore, Chennai", 1.5, 0, 1, "05:30", "12:00", "BUDGET", "spiritual,culture", chennai),
                place("Fort St. George", "India's first English fortress (1644) — now a museum housing the original East India Company artifacts.", "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600", "Rajaji Salai, Chennai", 2.0, 25, 2, "09:00", "17:00", "STANDARD", "history,solo", chennai),
                place("Dakshinachitra Heritage Museum", "Living museum of South Indian folk arts, crafts, architecture and heritage — 4 km from Muttukadu.", "https://images.unsplash.com/photo-1490750967868-88df5691cc13?w=600", "Muttukadu, ECR Road, 25 km from Chennai", 3.0, 200, 2, "10:00", "18:00", "STANDARD", "culture,family,art", chennai)
        ));

        guideRepo.save(guide("Divya Krishnamurthy", "Art historian and cultural guide specializing in Dravidian temples and colonial Chennai.", "https://api.dicebear.com/7.x/avataaars/svg?seed=Divya", 6, "Tamil, English, French", 4.7, 423, "XXXXXX3456", 1400, "Heritage, Temples, Art, Colonial", chennai));

        hotelRepo.saveAll(List.of(
                hotel("The Raintree Hotel", "Eco-certified 4-star hotel on Anna Salai — central location with organic restaurant.", "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=600", 4.4, 456, 4500, 2.5, "STANDARD", "WiFi, Pool, Eco-restaurant, Gym, Parking", "Anna Salai, Chennai", chennai),
                hotel("ITC Grand Chola", "Most luxurious hotel in South India — a modern-day Chola palace with every amenity imaginable.", "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=600", 4.9, 1200, 15000, 4.0, "LUXURY", "WiFi, Multiple pools, 5 restaurants, Spa, Grand ballroom, Butler service", "Mount Road, Chennai", chennai)
        ));

        log.info("  → Chennai seeded.");
    }

    // ================================================================
    // HELPER FACTORY METHODS
    // ================================================================
    private Place place(String name, String desc, String img, String loc, double hrs, int fee,
                        int day, String open, String close, String budget, String tags, Destination dest) {
        return Place.builder()
                .name(name).description(desc).imageUrl(img).location(loc)
                .visitingTimeHours(hrs).entryFee(fee).recommendedDay(day)
                .openTime(open).closeTime(close).budgetCategory(budget)
                .tags(tags).destination(dest).build();
    }

    private HiddenPlace hidden(String name, String loc, String desc, String why, String best,
                               double hrs, String img, String difficulty, Destination dest) {
        return HiddenPlace.builder()
                .name(name).location(loc).description(desc).whyVisit(why)
                .bestTimeToVisit(best).visitingTimeHours(hrs).imageUrl(img)
                .difficultyLevel(difficulty).destination(dest).build();
    }

    private Guide guide(String name, String bio, String photo, int exp, String langs,
                        double rating, int reviews, String contact, int price, String spec, Destination dest) {
        return Guide.builder()
                .name(name).bio(bio).photoUrl(photo).experienceYears(exp)
                .languages(langs).rating(rating).reviewCount(reviews)
                .contactMasked(contact).pricePerDay(price).specialization(spec)
                .destination(dest).build();
    }

    private Hotel hotel(String name, String desc, String img, double rating, int reviews,
                        int price, double dist, String budget, String facilities, String address, Destination dest) {
        return Hotel.builder()
                .name(name).description(desc).imageUrl(img).rating(rating)
                .reviewCount(reviews).pricePerNight(price).distanceFromCenter(dist)
                .budgetType(budget).facilities(facilities).address(address)
                .destination(dest).build();
    }
}
