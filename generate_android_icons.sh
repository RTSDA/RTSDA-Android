#!/bin/bash

# Create output directories
mkdir -p app/src/main/res/mipmap-{hdpi,mdpi,xhdpi,xxhdpi,xxxhdpi}

# Convert iOS icon to Android sizes
# mdpi (48x48)
convert "/Users/benjaminslingo/Development/RTSDA-iOS/RTSDA/Assets.xcassets/AppIcon.appiconset/RTSDA1024x1024.png" -resize 48x48 app/src/main/res/mipmap-mdpi/ic_launcher.png
convert "/Users/benjaminslingo/Development/RTSDA-iOS/RTSDA/Assets.xcassets/AppIcon.appiconset/RTSDA1024x1024.png" -resize 48x48 app/src/main/res/mipmap-mdpi/ic_launcher_round.png

# hdpi (72x72)
convert "/Users/benjaminslingo/Development/RTSDA-iOS/RTSDA/Assets.xcassets/AppIcon.appiconset/RTSDA1024x1024.png" -resize 72x72 app/src/main/res/mipmap-hdpi/ic_launcher.png
convert "/Users/benjaminslingo/Development/RTSDA-iOS/RTSDA/Assets.xcassets/AppIcon.appiconset/RTSDA1024x1024.png" -resize 72x72 app/src/main/res/mipmap-hdpi/ic_launcher_round.png

# xhdpi (96x96)
convert "/Users/benjaminslingo/Development/RTSDA-iOS/RTSDA/Assets.xcassets/AppIcon.appiconset/RTSDA1024x1024.png" -resize 96x96 app/src/main/res/mipmap-xhdpi/ic_launcher.png
convert "/Users/benjaminslingo/Development/RTSDA-iOS/RTSDA/Assets.xcassets/AppIcon.appiconset/RTSDA1024x1024.png" -resize 96x96 app/src/main/res/mipmap-xhdpi/ic_launcher_round.png

# xxhdpi (144x144)
convert "/Users/benjaminslingo/Development/RTSDA-iOS/RTSDA/Assets.xcassets/AppIcon.appiconset/RTSDA1024x1024.png" -resize 144x144 app/src/main/res/mipmap-xxhdpi/ic_launcher.png
convert "/Users/benjaminslingo/Development/RTSDA-iOS/RTSDA/Assets.xcassets/AppIcon.appiconset/RTSDA1024x1024.png" -resize 144x144 app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png

# xxxhdpi (192x192)
convert "/Users/benjaminslingo/Development/RTSDA-iOS/RTSDA/Assets.xcassets/AppIcon.appiconset/RTSDA1024x1024.png" -resize 192x192 app/src/main/res/mipmap-xxxhdpi/ic_launcher.png
convert "/Users/benjaminslingo/Development/RTSDA-iOS/RTSDA/Assets.xcassets/AppIcon.appiconset/RTSDA1024x1024.png" -resize 192x192 app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png

echo "Android app icons generated successfully!" 