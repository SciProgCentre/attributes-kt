# Changelog

## Unreleased

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security

## 0.4.0 - 2026-01-08

### Added

- Add `implies` property to show all attributes implied by attribute values in a set.
- Check implication conflicts when adding attributes to an `Attributes` instance. 
- Attribute now can imply other attribute values. For example, a triangular matrix is automatically a triangular matrix.
- attributes-kt-serialization advanced to `EXPERIMENTAL` support tier
- unsafe attributes builder `Attributes.unsafe`

### Changed

- Attributes `keys` moved to member API
- Attributes builders are now inline to support suspending inside the builder
- AttributesBuilder constructor is now public
