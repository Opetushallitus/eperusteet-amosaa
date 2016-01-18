var
    _             = require('lodash'),
    autoprefixer  = require('gulp-autoprefixer'),
    config        = require('./config.json'),
    concat        = require('gulp-concat'),
    connect       = require('gulp-connect'),
    preprocess    = require('gulp-preprocess'),
    gulp          = require('gulp'),
    jade          = require('gulp-jade'),
    proxy         = require('proxy-middleware'),
    sass          = require('gulp-sass'),
    shell         = require('gulp-shell'),
    spawn         = require('child_process').exec,
    typescript    = require('gulp-tsc'),
    url           = require('url'),
    ts_app_config = require('./tsconfig.json'),
    tslint        = require('gulp-tslint'),
    tslint_config = require('./tslint.json'),
    watch         = require('gulp-watch');

// TODO:
// Add preprocessing options
// Dist
// Tests
// Pack templates after compilation

function mkStream(fn) {
    return require('event-stream').map(fn);
}

function createProxy(from, to) {
    var pOpt = url.parse(from);
    pOpt.route = to;
    return proxy(pOpt);
}

gulp
.task('templatepacker', function() {
    return gulp.src([config.app + 'components/**/*.jade', config.app + 'views/**/*.jade', config.app + 'states/**/*.jade'])
        .pipe(mkStream(function(file, cb) {
            var fpath = file.path.slice((file.cwd + config.app).length + 1);
            if (fpath !== 'index.jade') {
                var prefix = 'script(type="text/ng-template" id="' + fpath + '")\n';
                file.contents = new Buffer(prefix + '  ' + String(file.contents).replace(/\n/g, '\n  ') + '\n');
            }
            cb(null, file);
        }))
        .pipe(concat('templates.jade'))
        .pipe(gulp.dest(config.app));
})
.task('templates', ['templatepacker'], function() {
    return gulp.src([config.app + 'index.jade'])
        .pipe(jade())
        .pipe(concat('index.html'))
        .pipe(gulp.dest(config.build))
        .pipe(connect.reload());
})
.task('static', function() {
    return gulp.src([
        './node_modules/bootstrap-sass/assets/fonts/*'
    ])
    .pipe(gulp.dest(config.build));
})
.task('sass', function() {
    return gulp.src([config.app + 'styles/styles.scss'])
        .pipe(sass().on('error', sass.logError))
        .pipe(concat('styles.css'))
        .pipe(autoprefixer({
            browser: ['last 1 version'],
            cascade: false
        }))
        .pipe(gulp.dest(config.build))
        .pipe(connect.reload());
})
.task('dependencies', function() {
    return gulp.src(config.dependencies)
        .pipe(concat('dependencies.js'))
        .pipe(gulp.dest(config.build));
})
// gulp-tsc uses file.path instead of file.contents
// .task('preprocess', function() {
//     return gulp.src(ts_app_config.files)
//         .pipe(mkStream(function(file, cb) {
//             var stateMatch = "app/states";
//             var state = file.path.indexOf(stateMatch);
//             if (state !== -1 && _.endsWith(file.path, 'state.ts')) {
//                 var stateName = file.path.slice(state + stateMatch.length);
//                 stateName = "root" + stateName.slice(0, stateName.length - "/state.ts".length).replace(/\//g, ".");
//                 file.contents = new Buffer(String(file.contents).replace(/\@StateName/g, stateName) + '\n');
//             }
//             cb(null, file);
//         }))
//         .pipe(gulp.dest('tmp/'));
// })
.task('compile', function() {
    return gulp.src(ts_app_config.files)
        .pipe(tslint({ configuration: tslint_config }))
        .pipe(tslint.report('prose', {
            emitError: false,
            summarizeFailureOutput: true
        }))
        .pipe(typescript())
        .on('error', function(error) {
            console.log(error.toString());
            this.emit('end');
        })
        .pipe(concat('app.js'))
        .pipe(gulp.dest(config.build))
        .pipe(connect.reload());
})
.task('connect', function() {
    return connect.server({
        root: config.build,
        port: 9030,
        livereload: {
            port: 35769
        },
        middleware: function (connect, opt) {
            return [
                createProxy('https://testi.virkailija.opintopolku.fi/eperusteet-service', '/eperusteet-service'),
                createProxy('http://localhost:8080/eperusteet-amosaa-service/', '/eperusteet-amosaa-service')
            ];
        }
    });
})
.task('build', ['dependencies', 'templates', 'sass', 'compile', 'static'])
.task('dist', ['build'], function() {
    return gulp.src([config.build + '**/*']).pipe(gulp.dest(config.dist));
})
.task('watch', ['connect', 'build'], function() {
    gulp.watch(config.app + '**/*.ts', ['compile']);
    gulp.watch(config.app + '**/*.jade', ['templates']);
    gulp.watch(config.app + 'styles/**/*.scss', ['sass']);
})
.task('default', ['watch']);
