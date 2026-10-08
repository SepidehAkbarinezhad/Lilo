// Deterministic PNG export of the same cubic paperclip used by LiloLogoMorph.kt.
// Run: xcrun swift scripts/generate-lilo-launcher-icons.swift <repository-root>
import Foundation
import CoreGraphics
import ImageIO
import UniformTypeIdentifiers

let root = CommandLine.arguments.count > 1 ? CommandLine.arguments[1] : FileManager.default.currentDirectoryPath
let wire = CGMutablePath()
wire.move(to: CGPoint(x: 4, y: -13)); wire.addLine(to: CGPoint(x: 4, y: 18))
wire.addCurve(to: CGPoint(x: -5, y: 27), control1: CGPoint(x: 4, y: 22.9706), control2: CGPoint(x: -0.0294, y: 27))
wire.addCurve(to: CGPoint(x: -14, y: 18), control1: CGPoint(x: -9.9706, y: 27), control2: CGPoint(x: -14, y: 22.9706))
wire.addLine(to: CGPoint(x: -14, y: -19))
wire.addCurve(to: CGPoint(x: 2, y: -35), control1: CGPoint(x: -14, y: -27.8366), control2: CGPoint(x: -6.8366, y: -35))
wire.addCurve(to: CGPoint(x: 18, y: -19), control1: CGPoint(x: 10.8366, y: -35), control2: CGPoint(x: 18, y: -27.8366))
wire.addLine(to: CGPoint(x: 18, y: 20))
wire.addCurve(to: CGPoint(x: -3.5, y: 41.5), control1: CGPoint(x: 18, y: 31.8741), control2: CGPoint(x: 8.3741, y: 41.5))
wire.addCurve(to: CGPoint(x: -25, y: 20), control1: CGPoint(x: -15.3741, y: 41.5), control2: CGPoint(x: -25, y: 31.8741))
wire.addLine(to: CGPoint(x: -25, y: -23))

func export(_ relative: String, size: Int, round: Bool = false, splash: Bool = false) throws {
    let n = CGFloat(size)
    let context = CGContext(data: nil, width: size, height: size, bitsPerComponent: 8, bytesPerRow: size * 4,
        space: CGColorSpace(name: CGColorSpace.sRGB)!, bitmapInfo: ((round || splash) ? CGImageAlphaInfo.premultipliedLast : CGImageAlphaInfo.noneSkipLast).rawValue)!
    context.translateBy(x: 0, y: n); context.scaleBy(x: 1, y: -1)
    context.setFillColor(CGColor(colorSpace: CGColorSpace(name: CGColorSpace.sRGB)!, components: [CGFloat(230)/255, CGFloat(74)/255, CGFloat(25)/255, 1])!)
    if splash {
        context.fillEllipse(in: CGRect(x: 0, y: 0, width: n, height: n))
    } else if round {
        context.fillEllipse(in: CGRect(x: 0, y: 0, width: n, height: n))
    } else {
        // Let iOS and adaptive Android masks round the icon; no baked-in white corners.
        context.fill(CGRect(x: 0, y: 0, width: n, height: n))
    }
    let radius = splash ? n / 2 : n * 0.39
    context.translateBy(x: n/2, y: n/2); context.scaleBy(x: radius/54, y: radius/54); context.rotate(by: -0.52)
    context.setStrokeColor(CGColor(gray: 1, alpha: 1)); context.setLineWidth(5.5); context.setLineCap(.round); context.setLineJoin(.round)
    context.addPath(wire); context.strokePath()
    let url = URL(fileURLWithPath: root).appendingPathComponent(relative)
    try FileManager.default.createDirectory(at: url.deletingLastPathComponent(), withIntermediateDirectories: true)
    let destination = CGImageDestinationCreateWithURL(url as CFURL, UTType.png.identifier as CFString, 1, nil)!
    CGImageDestinationAddImage(destination, context.makeImage()!, nil)
    guard CGImageDestinationFinalize(destination) else { throw NSError(domain: "LiloIconExport", code: 1) }
}
for (density, size) in [("mdpi",48),("hdpi",72),("xhdpi",96),("xxhdpi",144),("xxxhdpi",192)] {
    for name in ["ic_launcher", "ic_launcher_round"] {
        try export("composeApp/src/androidMain/res/mipmap-\(density)/\(name).png", size: size, round: name == "ic_launcher_round")
    }
}
try export("composeApp/src/androidMain/ic_launcher-playstore.png", size: 512)
try export("iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/lilo_icon.png", size: 1024)
try export("iosApp/iosApp/Assets.xcassets/SplashLogo.imageset/lilo_splash.png", size: 216, splash: true)
print("Exported launcher icons and launch logo.")
